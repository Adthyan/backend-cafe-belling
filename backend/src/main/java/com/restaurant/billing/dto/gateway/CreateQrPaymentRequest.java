package com.restaurant.billing.dto.gateway;

import jakarta.validation.constraints.NotNull;

public class CreateQrPaymentRequest {
    @NotNull
    private Long invoiceId;
    private Long closeAfterSeconds = 900L;

    public Long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Long invoiceId) { this.invoiceId = invoiceId; }
    public Long getCloseAfterSeconds() { return closeAfterSeconds; }
    public void setCloseAfterSeconds(Long closeAfterSeconds) { this.closeAfterSeconds = closeAfterSeconds; }
}

