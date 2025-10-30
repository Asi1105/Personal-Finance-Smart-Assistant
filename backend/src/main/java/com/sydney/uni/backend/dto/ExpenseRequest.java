package com.sydney.uni.backend.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ExpenseRequest {
    private String description;
    private String category; // Changed from ExpenseCategory to String
    private Double amount;
    private LocalDate date;
    private String notes;
}
