package com.sydney.uni.backend.dto;

public class BudgetComparisonDto {
    private String category;
    private Double budgeted;
    private Double spent;
    private Double remaining;

    public BudgetComparisonDto() {}

    public BudgetComparisonDto(String category, Double budgeted, Double spent, Double remaining) {
        this.category = category;
        this.budgeted = budgeted;
        this.spent = spent;
        this.remaining = remaining;
    }

    // Getters and Setters
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getBudgeted() {
        return budgeted;
    }

    public void setBudgeted(Double budgeted) {
        this.budgeted = budgeted;
    }

    public Double getSpent() {
        return spent;
    }

    public void setSpent(Double spent) {
        this.spent = spent;
    }

    public Double getRemaining() {
        return remaining;
    }

    public void setRemaining(Double remaining) {
        this.remaining = remaining;
    }
}


