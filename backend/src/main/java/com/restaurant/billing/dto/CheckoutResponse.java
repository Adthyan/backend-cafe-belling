package com.restaurant.billing.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class CheckoutResponse {

    private Long saleId;
    private Instant soldAt;
    private List<CheckoutLineResponse> lines;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal total;
    private String upiUri;

    public Long getSaleId() {
        return saleId;
    }

    public void setSaleId(Long saleId) {
        this.saleId = saleId;
    }

    public Instant getSoldAt() {
        return soldAt;
    }

    public void setSoldAt(Instant soldAt) {
        this.soldAt = soldAt;
    }

    public List<CheckoutLineResponse> getLines() {
        return lines;
    }

    public void setLines(List<CheckoutLineResponse> lines) {
        this.lines = lines;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getUpiUri() {
        return upiUri;
    }

    public void setUpiUri(String upiUri) {
        this.upiUri = upiUri;
    }
}
