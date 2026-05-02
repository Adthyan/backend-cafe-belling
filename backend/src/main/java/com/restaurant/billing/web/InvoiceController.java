package com.restaurant.billing.web;

import com.restaurant.billing.dto.gateway.CreateInvoiceRequest;
import com.restaurant.billing.dto.gateway.InvoiceResponse;
import com.restaurant.billing.service.InvoicePaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoicePaymentService invoicePaymentService;

    public InvoiceController(InvoicePaymentService invoicePaymentService) {
        this.invoicePaymentService = invoicePaymentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceResponse create(@Valid @RequestBody CreateInvoiceRequest request) {
        return invoicePaymentService.createInvoice(request);
    }
}

