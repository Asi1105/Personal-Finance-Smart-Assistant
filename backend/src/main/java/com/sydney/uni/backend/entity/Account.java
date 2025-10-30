package com.sydney.uni.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double balance;
    private Double saved;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
