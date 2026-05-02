package com.restaurant.billing.web;

import com.restaurant.billing.dto.gateway.CreateQrPaymentRequest;
import com.restaurant.billing.dto.gateway.PaymentMonitorRowResponse;
import com.restaurant.billing.dto.gateway.PaymentQrResponse;
import com.restaurant.billing.dto.gateway.PaymentStatusResponse;
import com.restaurant.billing.dto.UpiUriResponse;
import com.restaurant.billing.service.InvoicePaymentService;
import com.restaurant.billing.service.SaleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final SaleService saleService;
    private final InvoicePaymentService invoicePaymentService;

    public PaymentController(SaleService saleService, InvoicePaymentService invoicePaymentService) {
        this.saleService = saleService;
        this.invoicePaymentService = invoicePaymentService;
    }

    @GetMapping("/upi-uri")
    public UpiUriResponse upiUri(@RequestParam Long saleId) {
        return new UpiUriResponse(saleService.upiUriForSale(saleId));
    }

    @PostMapping("/qr")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentQrResponse createQr(@Valid @RequestBody CreateQrPaymentRequest request) {
        return invoicePaymentService.createDynamicQr(request);
    }

    @GetMapping("/{paymentId}/status")
    public PaymentStatusResponse status(@PathVariable Long paymentId) {
        return invoicePaymentService.paymentStatus(paymentId);
    }

    @GetMapping("/monitor")
    public List<PaymentMonitorRowResponse> monitor() {
        return invoicePaymentService.monitorRows();
    }

    @PostMapping("/webhook/razorpay")
    @ResponseStatus(HttpStatus.OK)
    public void webhook(
            @RequestBody String payload,
            @RequestHeader(value = "X-Razorpay-Signature", required = false) String signature) {
        invoicePaymentService.handleRazorpayWebhook(payload, signature == null ? "" : signature);
    }
}
