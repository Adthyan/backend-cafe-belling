package com.restaurant.billing.dto;

import java.math.BigDecimal;

public class CheckoutLineResponse {

    private Long menuItemId;
    private String name;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;

    public CheckoutLineResponse() {}

    public CheckoutLineResponse(Long menuItemId, String name, int quantity, BigDecimal unitPrice, BigDecimal lineTotal) {
        this.menuItemId = menuItemId;
        this.name = name;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.lineTotal = lineTotal;
    }

    public Long getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(Long menuItemId) {
        this.menuItemId = menuItemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }
}
