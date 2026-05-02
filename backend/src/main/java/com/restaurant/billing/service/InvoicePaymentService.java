package com.restaurant.billing.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.billing.dto.gateway.CreateInvoiceRequest;
import com.restaurant.billing.dto.gateway.CreateQrPaymentRequest;
import com.restaurant.billing.dto.gateway.InvoiceResponse;
import com.restaurant.billing.dto.gateway.PaymentQrResponse;
import com.restaurant.billing.dto.gateway.PaymentMonitorRowResponse;
import com.restaurant.billing.dto.gateway.PaymentStatusResponse;
import com.restaurant.billing.entity.Invoice;
import com.restaurant.billing.entity.Payment;
import com.restaurant.billing.entity.Sale;
import com.restaurant.billing.entity.enums.InvoiceStatus;
import com.restaurant.billing.entity.enums.PaymentStatus;
import com.restaurant.billing.repository.InvoiceRepository;
import com.restaurant.billing.repository.PaymentRepository;
import com.restaurant.billing.repository.SaleRepository;
import com.restaurant.billing.util.WebhookSignatureUtil;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class InvoicePaymentService {
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final SaleRepository saleRepository;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ShopSettingsService shopSettingsService;

    @Value("${razorpay.base-url:https://api.razorpay.com}")
    private String razorpayBaseUrl;
    @Value("${razorpay.key-id:}")
    private String razorpayKeyId;
    @Value("${razorpay.key-secret:}")
    private String razorpayKeySecret;
    @Value("${razorpay.webhook-secret:}")
    private String razorpayWebhookSecret;

    public InvoicePaymentService(
            InvoiceRepository invoiceRepository,
            PaymentRepository paymentRepository,
            SaleRepository saleRepository,
            ObjectMapper objectMapper,
            ShopSettingsService shopSettingsService) {
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
        this.saleRepository = saleRepository;
        this.objectMapper = objectMapper;
        this.shopSettingsService = shopSettingsService;
    }

    @Transactional
    public InvoiceResponse createInvoice(CreateInvoiceRequest request) {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        invoice.setCustomerName(request.getCustomerName().trim());
        invoice.setAmount(request.getAmount().setScale(2, RoundingMode.HALF_UP));
        invoice.setCurrency((request.getCurrency() == null || request.getCurrency().isBlank()) ? "INR" : request.getCurrency().trim().toUpperCase());
        invoice.setDescription(request.getDescription());
        invoice.setStatus(InvoiceStatus.UNPAID);
        invoice.setCreatedAt(Instant.now());
        invoice.setUpdatedAt(Instant.now());
        return toInvoiceResponse(invoiceRepository.save(invoice));
    }

    @Transactional
    public PaymentQrResponse createDynamicQr(CreateQrPaymentRequest request) {
        Invoice invoice =
                invoiceRepository
                        .findById(request.getInvoiceId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));
        return startNewPaymentWithGatewayQr(invoice, request.getCloseAfterSeconds());
    }

    /**
     * POS checkout: creates an invoice for the sale (if needed) and returns a Razorpay dynamic QR (or UPI fallback).
     * When payment is captured (webhook or status poll), the linked sale is marked PAID.
     */
    @Transactional
    public PaymentQrResponse createGatewayQrForSale(Long saleId, Long closeAfterSeconds) {
        Sale sale =
                saleRepository
                        .findById(saleId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sale not found"));
        if (!"PENDING".equalsIgnoreCase(sale.getPaymentStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sale is not awaiting payment");
        }

        Invoice invoice =
                invoiceRepository
                        .findFirstBySale_IdOrderByCreatedAtDesc(saleId)
                        .orElseGet(
                                () -> {
                                    Invoice inv = new Invoice();
                                    inv.setInvoiceNumber("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                                    inv.setCustomerName("Walk-in");
                                    inv.setAmount(sale.getTotal().setScale(2, RoundingMode.HALF_UP));
                                    inv.setCurrency("INR");
                                    inv.setDescription("Restaurant bill #" + sale.getId());
                                    inv.setSale(sale);
                                    inv.setStatus(InvoiceStatus.UNPAID);
                                    inv.setCreatedAt(Instant.now());
                                    inv.setUpdatedAt(Instant.now());
                                    return invoiceRepository.save(inv);
                                });

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sale already paid");
        }

        Optional<Payment> pending =
                paymentRepository.findFirstByInvoice_IdAndStatusOrderByCreatedAtDesc(invoice.getId(), PaymentStatus.PENDING);
        if (pending.isPresent()) {
            return toPaymentQrResponse(pending.get());
        }

        return startNewPaymentWithGatewayQr(invoice, closeAfterSeconds);
    }

    @Transactional
    public PaymentStatusResponse paymentStatus(Long paymentId) {
        refreshPendingPaymentFromRazorpay(paymentId);
        Payment payment =
                paymentRepository.findById(paymentId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
        syncSaleIfInvoicePaid(payment);
        return toPaymentStatusResponse(payment);
    }

    /**
     * Without a public webhook URL (typical on localhost), payments never reach {@link #handleRazorpayWebhook}.
     * Poll Razorpay when the client checks status so captured UPI QR payments flip to PAID automatically.
     */
    private void refreshPendingPaymentFromRazorpay(Long paymentId) {
        Payment payment =
                paymentRepository.findById(paymentId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
        if (payment.getStatus() != PaymentStatus.PENDING) {
            return;
        }
        if (razorpayKeyId == null || razorpayKeyId.isBlank() || razorpayKeySecret == null || razorpayKeySecret.isBlank()) {
            return;
        }
        String qrId = payment.getGatewayQrId();
        if (qrId == null || qrId.startsWith("SIM-")) {
            return;
        }

        HttpHeaders headers = razorpayAuthHeaders();
        try {
            String qrPaymentsUrl = razorpayBaseUrl + "/v1/payments/qr_codes/" + qrId + "/payments?count=20";
            ResponseEntity<String> qrResp =
                    restTemplate.exchange(qrPaymentsUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);
            JsonNode root = objectMapper.readTree(qrResp.getBody());
            JsonNode items = root.path("items");
            if (items.isArray()) {
                for (JsonNode p : items) {
                    if (applyIfCapturedRazorpayPayment(payment, p)) {
                        return;
                    }
                }
            }
        } catch (Exception ignored) {
            // Status poll must still work if Razorpay is temporarily unreachable
        }

        try {
            String listUrl = razorpayBaseUrl + "/v1/payments?count=100";
            ResponseEntity<String> listResp =
                    restTemplate.exchange(listUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);
            JsonNode root = objectMapper.readTree(listResp.getBody());
            JsonNode items = root.path("items");
            if (!items.isArray()) {
                return;
            }
            String ref = payment.getReferenceId();
            for (JsonNode p : items) {
                String noteRef = readNoteInternalRef(p);
                if (ref.equals(noteRef) && applyIfCapturedRazorpayPayment(payment, p)) {
                    return;
                }
            }
        } catch (Exception ignored) {
            // ignore
        }
    }

    private HttpHeaders razorpayAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String auth =
                Base64.getEncoder().encodeToString((razorpayKeyId + ":" + razorpayKeySecret).getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + auth);
        return headers;
    }

    private static String readNoteInternalRef(JsonNode paymentNode) {
        JsonNode notes = paymentNode.path("notes");
        if (notes.isMissingNode() || notes.isNull()) {
            return null;
        }
        JsonNode ref = notes.path("internalPaymentRef");
        return ref.isMissingNode() || ref.isNull() ? null : ref.asText(null);
    }

    /** Returns true if this row was a captured payment applied to our pending payment. */
    private boolean applyIfCapturedRazorpayPayment(Payment payment, JsonNode razorpayPaymentNode) {
        String status = text(razorpayPaymentNode, "status");
        if (!"captured".equals(status)) {
            return false;
        }
        long amountPaise = razorpayPaymentNode.path("amount").asLong(0);
        long expectedPaise = payment.getAmount().multiply(BigDecimal.valueOf(100)).longValue();
        if (amountPaise != expectedPaise) {
            return false;
        }
        String gatewayPayId = text(razorpayPaymentNode, "id");
        payment.setStatus(PaymentStatus.PAID);
        payment.setGatewayPaymentId(gatewayPayId);
        payment.setUpdatedAt(Instant.now());
        payment.getInvoice().setStatus(InvoiceStatus.PAID);
        payment.getInvoice().setUpdatedAt(Instant.now());
        paymentRepository.save(payment);
        invoiceRepository.save(payment.getInvoice());
        syncSaleIfInvoicePaid(payment);
        return true;
    }

    private PaymentQrResponse startNewPaymentWithGatewayQr(Invoice invoice, Long closeAfterSeconds) {
        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setReferenceId("PAYREF-" + UUID.randomUUID().toString().replace("-", "").substring(0, 18));
        payment.setAmount(invoice.getAmount());
        payment.setCurrency(invoice.getCurrency());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(Instant.now());
        payment.setUpdatedAt(Instant.now());
        payment = paymentRepository.save(payment);

        if (razorpayKeyId == null || razorpayKeyId.isBlank() || razorpayKeySecret == null || razorpayKeySecret.isBlank()) {
            String fallbackUri =
                    new UpiPaymentService()
                            .buildUpiUri(shopSettingsService.getOrCreate(), payment.getAmount(), "Invoice " + invoice.getInvoiceNumber());
            payment.setGatewayQrId("SIM-" + payment.getId());
            payment.setGatewayQrContent(fallbackUri);
            paymentRepository.save(payment);
            return toPaymentQrResponse(payment);
        }

        long amountPaise = payment.getAmount().multiply(BigDecimal.valueOf(100)).longValue();
        long closeByEpoch =
                Instant.now().getEpochSecond() + Math.max(60L, closeAfterSeconds == null ? 900L : closeAfterSeconds);

        Map<String, Object> body =
                Map.of(
                        "type", "upi_qr",
                        "name", "Invoice " + invoice.getInvoiceNumber(),
                        "usage", "single_use",
                        "fixed_amount", true,
                        "payment_amount", amountPaise,
                        "description", invoice.getDescription() == null ? "Restaurant bill payment" : invoice.getDescription(),
                        "close_by", closeByEpoch,
                        "notes", Map.of("internalPaymentRef", payment.getReferenceId(), "invoiceNumber", invoice.getInvoiceNumber()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String auth = Base64.getEncoder().encodeToString((razorpayKeyId + ":" + razorpayKeySecret).getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + auth);

        ResponseEntity<String> response =
                restTemplate.exchange(
                        razorpayBaseUrl + "/v1/payments/qr_codes",
                        HttpMethod.POST,
                        new HttpEntity<>(body, headers),
                        String.class);

        try {
            JsonNode node = objectMapper.readTree(response.getBody());
            payment.setGatewayQrId(text(node, "id"));
            payment.setGatewayQrImageUrl(text(node, "image_url"));
            payment.setGatewayQrContent(text(node, "short_url"));
            payment.setUpdatedAt(Instant.now());
            paymentRepository.save(payment);
            return toPaymentQrResponse(payment);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Could not parse gateway QR response");
        }
    }

    private void syncSaleIfInvoicePaid(Payment payment) {
        if (payment.getStatus() != PaymentStatus.PAID) {
            return;
        }
        Invoice invoice = payment.getInvoice();
        if (invoice.getSale() == null) {
            return;
        }
        Sale sale = invoice.getSale();
        if (!"PAID".equalsIgnoreCase(sale.getPaymentStatus())) {
            sale.setPaymentStatus("PAID");
            sale.setPaymentNote("RAZORPAY");
            saleRepository.save(sale);
        }
    }

    @Transactional(readOnly = true)
    public List<PaymentMonitorRowResponse> monitorRows() {
        return paymentRepository.findAllByOrderByCreatedAtDesc().stream().map(this::toMonitorRow).toList();
    }

    @Transactional
    public void handleRazorpayWebhook(String payload, String signatureHeader) {
        if (razorpayWebhookSecret != null && !razorpayWebhookSecret.isBlank()) {
            String expected = WebhookSignatureUtil.hmacSha256Hex(payload, razorpayWebhookSecret);
            if (!expected.equals(signatureHeader)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid webhook signature");
            }
        }
        try {
            JsonNode root = objectMapper.readTree(payload);
            String event = text(root, "event");
            String reference = extractReference(root);
            String gatewayPaymentId = extractGatewayPaymentId(root);
            Payment payment = findPayment(reference, gatewayPaymentId);
            if (payment == null) {
                return;
            }

            if ("payment.captured".equals(event) || "qr_code.credited".equals(event)) {
                payment.setStatus(PaymentStatus.PAID);
                payment.getInvoice().setStatus(InvoiceStatus.PAID);
                syncSaleIfInvoicePaid(payment);
            } else if ("payment.failed".equals(event)) {
                payment.setStatus(PaymentStatus.FAILED);
                payment.getInvoice().setStatus(InvoiceStatus.FAILED);
                payment.setFailureReason(readAt(root, "payload.payment.entity.error_description"));
            } else if ("qr_code.closed".equals(event)) {
                if (payment.getStatus() == PaymentStatus.PENDING) {
                    payment.setStatus(PaymentStatus.EXPIRED);
                }
            }
            if (gatewayPaymentId != null && !gatewayPaymentId.isBlank()) {
                payment.setGatewayPaymentId(gatewayPaymentId);
            }
            payment.setLastWebhookAt(Instant.now());
            payment.setUpdatedAt(Instant.now());
            payment.getInvoice().setUpdatedAt(Instant.now());
            paymentRepository.save(payment);
            invoiceRepository.save(payment.getInvoice());
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid webhook payload");
        }
    }

    private Payment findPayment(String reference, String gatewayPaymentId) {
        if (reference != null && !reference.isBlank()) {
            return paymentRepository.findByReferenceId(reference).orElse(null);
        }
        if (gatewayPaymentId != null && !gatewayPaymentId.isBlank()) {
            return paymentRepository.findByGatewayPaymentId(gatewayPaymentId).orElse(null);
        }
        return null;
    }

    private String extractReference(JsonNode root) {
        String fromPayment = readAt(root, "payload.payment.entity.notes.internalPaymentRef");
        if (fromPayment != null && !fromPayment.isBlank()) {
            return fromPayment;
        }
        return readAt(root, "payload.qr_code.entity.notes.internalPaymentRef");
    }

    private String extractGatewayPaymentId(JsonNode root) {
        String id = readAt(root, "payload.payment.entity.id");
        if (id != null && !id.isBlank()) {
            return id;
        }
        return readAt(root, "payload.payment_link.entity.payment_id");
    }

    private String readAt(JsonNode root, String path) {
        JsonNode current = root;
        for (String segment : path.split("\\.")) {
            if (current == null || current.isMissingNode() || current.isNull()) {
                return null;
            }
            current = current.path(segment);
        }
        return current.isMissingNode() || current.isNull() ? null : current.asText(null);
    }

    private static String text(JsonNode node, String key) {
        JsonNode val = node.path(key);
        return val.isMissingNode() || val.isNull() ? null : val.asText();
    }

    private InvoiceResponse toInvoiceResponse(Invoice invoice) {
        InvoiceResponse response = new InvoiceResponse();
        response.setId(invoice.getId());
        response.setInvoiceNumber(invoice.getInvoiceNumber());
        response.setCustomerName(invoice.getCustomerName());
        response.setAmount(invoice.getAmount());
        response.setCurrency(invoice.getCurrency());
        response.setDescription(invoice.getDescription());
        response.setStatus(invoice.getStatus());
        response.setCreatedAt(invoice.getCreatedAt());
        return response;
    }

    private PaymentQrResponse toPaymentQrResponse(Payment payment) {
        PaymentQrResponse response = new PaymentQrResponse();
        response.setPaymentId(payment.getId());
        response.setInvoiceId(payment.getInvoice().getId());
        response.setInvoiceNumber(payment.getInvoice().getInvoiceNumber());
        response.setGatewayQrId(payment.getGatewayQrId());
        response.setQrImageUrl(payment.getGatewayQrImageUrl());
        response.setQrContent(payment.getGatewayQrContent());
        response.setStatus(payment.getStatus());
        response.setAmount(payment.getAmount());
        response.setCurrency(payment.getCurrency());
        response.setCreatedAt(payment.getCreatedAt());
        return response;
    }

    private PaymentStatusResponse toPaymentStatusResponse(Payment payment) {
        PaymentStatusResponse response = new PaymentStatusResponse();
        response.setPaymentId(payment.getId());
        response.setInvoiceId(payment.getInvoice().getId());
        response.setPaymentStatus(payment.getStatus());
        response.setInvoiceStatus(payment.getInvoice().getStatus());
        response.setGatewayPaymentId(payment.getGatewayPaymentId());
        response.setFailureReason(payment.getFailureReason());
        return response;
    }

    private PaymentMonitorRowResponse toMonitorRow(Payment payment) {
        PaymentMonitorRowResponse row = new PaymentMonitorRowResponse();
        row.setPaymentId(payment.getId());
        row.setInvoiceNumber(payment.getInvoice().getInvoiceNumber());
        row.setCustomerName(payment.getInvoice().getCustomerName());
        row.setAmount(payment.getAmount());
        row.setCurrency(payment.getCurrency());
        row.setPaymentStatus(payment.getStatus());
        row.setInvoiceStatus(payment.getInvoice().getStatus());
        row.setGatewayPaymentId(payment.getGatewayPaymentId());
        row.setFailureReason(payment.getFailureReason());
        row.setCreatedAt(payment.getCreatedAt());
        row.setUpdatedAt(payment.getUpdatedAt());
        return row;
    }
}

