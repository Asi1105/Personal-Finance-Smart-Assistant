package com.sydney.uni.backend.dto;

import java.util.List;

public class ReportsDto {
    private List<MonthlyDataDto> monthlyData;
    private List<CategoryExpenseDto> categoryExpenses;
    private List<BudgetComparisonDto> budgetComparison;
    private ReportsMetricsDto metrics;

    public ReportsDto() {}

    public ReportsDto(List<MonthlyDataDto> monthlyData, List<CategoryExpenseDto> categoryExpenses, 
                     List<BudgetComparisonDto> budgetComparison, ReportsMetricsDto metrics) {
        this.monthlyData = monthlyData;
        this.categoryExpenses = categoryExpenses;
        this.budgetComparison = budgetComparison;
        this.metrics = metrics;
    }

    // Getters and Setters
    public List<MonthlyDataDto> getMonthlyData() {
        return monthlyData;
    }

    public void setMonthlyData(List<MonthlyDataDto> monthlyData) {
        this.monthlyData = monthlyData;
    }

    public List<CategoryExpenseDto> getCategoryExpenses() {
        return categoryExpenses;
    }

    public void setCategoryExpenses(List<CategoryExpenseDto> categoryExpenses) {
        this.categoryExpenses = categoryExpenses;
    }

    public List<BudgetComparisonDto> getBudgetComparison() {
        return budgetComparison;
    }

    public void setBudgetComparison(List<BudgetComparisonDto> budgetComparison) {
        this.budgetComparison = budgetComparison;
    }

    public ReportsMetricsDto getMetrics() {
        return metrics;
    }

    public void setMetrics(ReportsMetricsDto metrics) {
        this.metrics = metrics;
    }
}

