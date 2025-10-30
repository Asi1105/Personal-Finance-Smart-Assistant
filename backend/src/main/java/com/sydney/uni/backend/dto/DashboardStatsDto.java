package com.sydney.uni.backend.dto;

import lombok.Data;

@Data
public class DashboardStatsDto {
    private Double totalBalance;
    private Double saved;
    private Double monthlySpending;
    private Double budgetLeft;
    private Double savingsGoal;
    private Double savingsProgress;
    private Double budgetUsedPercentage;
    private Double monthlySpendingChange;
    private Double lastMonthSpending; // Last month's spending amount
    private Boolean hasSavingsGoal; // Whether user has set a savings goal
}
