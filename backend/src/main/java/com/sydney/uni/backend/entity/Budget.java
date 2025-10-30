package com.sydney.uni.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;
    private String period; // day / week / month
    private Double amount;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
