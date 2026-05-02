package com.restaurant.billing.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DailySaleDto {

    private LocalDate date;
    private long saleCount;
    private BigDecimal revenue;

    public DailySaleDto() {}

    public DailySaleDto(LocalDate date, long saleCount, BigDecimal revenue) {
        this.date = date;
        this.saleCount = saleCount;
        this.revenue = revenue;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public long getSaleCount() {
        return saleCount;
    }

    public void setSaleCount(long saleCount) {
        this.saleCount = saleCount;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }
}
