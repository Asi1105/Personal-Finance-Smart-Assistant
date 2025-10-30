package com.sydney.uni.backend.dto;

import lombok.Data;

@Data
public class BudgetDto {
    private Long id;
    private String category;
    private String period;
    private Double amount;
    private Double spent;
    private Double remaining;
    private Double utilizationPercentage;
}
