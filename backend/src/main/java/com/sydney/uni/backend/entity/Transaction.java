package com.sydney.uni.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private TransactionType type; // in/out
    private LocalDate date;
    private ExpenseCategory expenseCategory;
    private String detail;
    private Double amount;
    private String note;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;


}
