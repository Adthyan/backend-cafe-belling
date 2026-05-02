package com.restaurant.billing.dto.gateway;

import com.restaurant.billing.entity.enums.InvoiceStatus;
import com.restaurant.billing.entity.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.Instant;

public class PaymentMonitorRowResponse {
    private Long paymentId;
    private String invoiceNumber;
    private String customerName;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus paymentStatus;
    private InvoiceStatus invoiceStatus;
    private String gatewayPaymentId;
    private String failureReason;
    private Instant createdAt;
    private Instant updatedAt;

    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    public InvoiceStatus getInvoiceStatus() { return invoiceStatus; }
    public void setInvoiceStatus(InvoiceStatus invoiceStatus) { this.invoiceStatus = invoiceStatus; }
    public String getGatewayPaymentId() { return gatewayPaymentId; }
    public void setGatewayPaymentId(String gatewayPaymentId) { this.gatewayPaymentId = gatewayPaymentId; }
    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}

