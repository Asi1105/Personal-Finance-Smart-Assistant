package com.sydney.uni.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CategoryExpenseDto {
    // Getters and Setters
    private String category;
    private Double amount;
    private String color;
    private String percentage;

    public CategoryExpenseDto(String category, Double amount, String color, String percentage) {
        this.category = category;
        this.amount = amount;
        this.color = color;
        this.percentage = percentage;
    }

}


