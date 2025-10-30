package com.sydney.uni.backend.dto;

public class MonthlyDataDto {
    private String month;
    private Double income;
    private Double expenses;
    private Double savings;

    public MonthlyDataDto() {}

    public MonthlyDataDto(String month, Double income, Double expenses, Double savings) {
        this.month = month;
        this.income = income;
        this.expenses = expenses;
        this.savings = savings;
    }

    // Getters and Setters
    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public Double getIncome() {
        return income;
    }

    public void setIncome(Double income) {
        this.income = income;
    }

    public Double getExpenses() {
        return expenses;
    }

    public void setExpenses(Double expenses) {
        this.expenses = expenses;
    }

    public Double getSavings() {
        return savings;
    }

    public void setSavings(Double savings) {
        this.savings = savings;
    }
}


