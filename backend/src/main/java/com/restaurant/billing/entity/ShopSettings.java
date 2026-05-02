package com.restaurant.billing.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "shop_settings")
public class ShopSettings {

    @Id
    private Long id = 1L;

    @Column(nullable = false)
    private String merchantVpa = "merchant@upi";

    @Column(nullable = false)
    private String merchantName = "Restaurant";

    @Column(nullable = false, length = 8)
    private String currency = "INR";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
