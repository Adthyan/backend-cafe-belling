package com.restaurant.billing.dto;

import jakarta.validation.constraints.NotBlank;

public class ShopSettingsDto {

    @NotBlank
    private String merchantVpa;

    @NotBlank
    private String merchantName;

    private String currency = "INR";

    public String getMerchantVpa() {
        return merchantVpa;
    }

    public void setMerchantVpa(String merchantVpa) {
        this.merchantVpa = merchantVpa;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
