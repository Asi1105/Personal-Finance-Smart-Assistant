package com.sydney.uni.backend.dto;

import com.sydney.uni.backend.entity.ExpenseCategory;
import com.sydney.uni.backend.entity.TransactionType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TransactionDto {
    private Long id;
    private TransactionType type;
    private LocalDate date;
    private ExpenseCategory expenseCategory;
    private String detail;
    private Double amount;
    private String note;
    private String categoryDisplayName;
    private String icon;
}
