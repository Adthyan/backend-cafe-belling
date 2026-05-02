package com.restaurant.billing.dto.gateway;

import com.restaurant.billing.entity.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.Instant;

public class PaymentQrResponse {
    private Long paymentId;
    private Long invoiceId;
    private String invoiceNumber;
    private String gatewayQrId;
    private String qrImageUrl;
    private String qrContent;
    private PaymentStatus status;
    private BigDecimal amount;
    private String currency;
    private Instant createdAt;

    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }
    public Long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Long invoiceId) { this.invoiceId = invoiceId; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    public String getGatewayQrId() { return gatewayQrId; }
    public void setGatewayQrId(String gatewayQrId) { this.gatewayQrId = gatewayQrId; }
    public String getQrImageUrl() { return qrImageUrl; }
    public void setQrImageUrl(String qrImageUrl) { this.qrImageUrl = qrImageUrl; }
    public String getQrContent() { return qrContent; }
    public void setQrContent(String qrContent) { this.qrContent = qrContent; }
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}

