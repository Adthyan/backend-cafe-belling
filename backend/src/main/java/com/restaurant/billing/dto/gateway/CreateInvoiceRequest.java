package com.restaurant.billing.dto.gateway;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CreateInvoiceRequest {
    @NotBlank
    private String customerName;
    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;
    private String description;
    private String currency = "INR";

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}

