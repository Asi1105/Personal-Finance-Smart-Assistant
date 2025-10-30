package com.sydney.uni.backend.dto;

public class ReportsMetricsDto {
    private Double totalIncome;
    private Double totalExpenses;
    private Double totalSavings;
    private Double avgMonthlyExpenses;
    private Double savingsRate;

    public ReportsMetricsDto() {}

    public ReportsMetricsDto(Double totalIncome, Double totalExpenses, Double totalSavings, 
                           Double avgMonthlyExpenses, Double savingsRate) {
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
        this.totalSavings = totalSavings;
        this.avgMonthlyExpenses = avgMonthlyExpenses;
        this.savingsRate = savingsRate;
    }

    // Getters and Setters
    public Double getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(Double totalIncome) {
        this.totalIncome = totalIncome;
    }

    public Double getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(Double totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public Double getTotalSavings() {
        return totalSavings;
    }

    public void setTotalSavings(Double totalSavings) {
        this.totalSavings = totalSavings;
    }

    public Double getAvgMonthlyExpenses() {
        return avgMonthlyExpenses;
    }

    public void setAvgMonthlyExpenses(Double avgMonthlyExpenses) {
        this.avgMonthlyExpenses = avgMonthlyExpenses;
    }

    public Double getSavingsRate() {
        return savingsRate;
    }

    public void setSavingsRate(Double savingsRate) {
        this.savingsRate = savingsRate;
    }
}


