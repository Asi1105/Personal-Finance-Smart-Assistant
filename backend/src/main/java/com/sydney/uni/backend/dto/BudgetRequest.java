package com.sydney.uni.backend.dto;

import lombok.Data;

@Data
public class BudgetRequest {
    private String category;
    private Double amount;
    private String period;
}
