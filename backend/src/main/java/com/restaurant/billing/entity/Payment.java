package com.restaurant.billing.entity;

import com.restaurant.billing.entity.enums.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(nullable = false, length = 32)
    private String gateway = "RAZORPAY";

    @Column(nullable = false, unique = true, length = 64)
    private String referenceId;

    @Column(length = 128)
    private String gatewayQrId;

    @Column(length = 512)
    private String gatewayQrImageUrl;

    @Column(length = 2048)
    private String gatewayQrContent;

    @Column(length = 128)
    private String gatewayPaymentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 8)
    private String currency = "INR";

    private String failureReason;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    private Instant lastWebhookAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getGatewayQrId() {
        return gatewayQrId;
    }

    public void setGatewayQrId(String gatewayQrId) {
        this.gatewayQrId = gatewayQrId;
    }

    public String getGatewayQrImageUrl() {
        return gatewayQrImageUrl;
    }

    public void setGatewayQrImageUrl(String gatewayQrImageUrl) {
        this.gatewayQrImageUrl = gatewayQrImageUrl;
    }

    public String getGatewayQrContent() {
        return gatewayQrContent;
    }

    public void setGatewayQrContent(String gatewayQrContent) {
        this.gatewayQrContent = gatewayQrContent;
    }

    public String getGatewayPaymentId() {
        return gatewayPaymentId;
    }

    public void setGatewayPaymentId(String gatewayPaymentId) {
        this.gatewayPaymentId = gatewayPaymentId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getLastWebhookAt() {
        return lastWebhookAt;
    }

    public void setLastWebhookAt(Instant lastWebhookAt) {
        this.lastWebhookAt = lastWebhookAt;
    }
}

