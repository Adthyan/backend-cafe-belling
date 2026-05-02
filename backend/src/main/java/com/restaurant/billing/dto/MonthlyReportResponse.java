package com.restaurant.billing.dto;

import java.math.BigDecimal;
import java.util.List;

public class MonthlyReportResponse {

    private int year;
    private int month;
    private long totalSaleCount;
    private BigDecimal totalRevenue;
    private List<DailySaleDto> dailyBreakdown;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public long getTotalSaleCount() {
        return totalSaleCount;
    }

    public void setTotalSaleCount(long totalSaleCount) {
        this.totalSaleCount = totalSaleCount;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public List<DailySaleDto> getDailyBreakdown() {
        return dailyBreakdown;
    }

    public void setDailyBreakdown(List<DailySaleDto> dailyBreakdown) {
        this.dailyBreakdown = dailyBreakdown;
    }
}
