package com.restaurant.billing.dto.gateway;

import com.restaurant.billing.entity.enums.InvoiceStatus;
import com.restaurant.billing.entity.enums.PaymentStatus;

public class PaymentStatusResponse {
    private Long paymentId;
    private Long invoiceId;
    private PaymentStatus paymentStatus;
    private InvoiceStatus invoiceStatus;
    private String gatewayPaymentId;
    private String failureReason;

    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }
    public Long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Long invoiceId) { this.invoiceId = invoiceId; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    public InvoiceStatus getInvoiceStatus() { return invoiceStatus; }
    public void setInvoiceStatus(InvoiceStatus invoiceStatus) { this.invoiceStatus = invoiceStatus; }
    public String getGatewayPaymentId() { return gatewayPaymentId; }
    public void setGatewayPaymentId(String gatewayPaymentId) { this.gatewayPaymentId = gatewayPaymentId; }
    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
}

