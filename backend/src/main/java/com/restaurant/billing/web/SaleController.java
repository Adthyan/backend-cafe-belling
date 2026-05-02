package com.restaurant.billing.web;

import com.restaurant.billing.dto.CheckoutRequest;
import com.restaurant.billing.dto.CheckoutResponse;
import com.restaurant.billing.dto.MonthlyReportResponse;
import com.restaurant.billing.dto.gateway.PaymentQrResponse;
import com.restaurant.billing.service.InvoicePaymentService;
import com.restaurant.billing.service.SaleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final SaleService saleService;
    private final InvoicePaymentService invoicePaymentService;

    public SaleController(SaleService saleService, InvoicePaymentService invoicePaymentService) {
        this.saleService = saleService;
        this.invoicePaymentService = invoicePaymentService;
    }

    @PostMapping("/checkout")
    public CheckoutResponse checkout(@Valid @RequestBody CheckoutRequest request) {
        return saleService.checkout(request);
    }

    @GetMapping("/monthly")
    public MonthlyReportResponse monthly(
            @RequestParam int year, @RequestParam int month) {
        return saleService.monthlyReport(year, month);
    }

    @PutMapping("/{saleId}/mark-paid")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markPaid(@PathVariable Long saleId) {
        saleService.markPaid(saleId);
    }

    /** Razorpay (or UPI fallback) QR for an unpaid POS sale — linked invoice marks the sale paid when payment clears. */
    @PostMapping("/{saleId}/gateway-qr")
    public PaymentQrResponse gatewayQr(
            @PathVariable Long saleId, @RequestParam(required = false) Long closeAfterSeconds) {
        return invoicePaymentService.createGatewayQrForSale(saleId, closeAfterSeconds);
    }
}
