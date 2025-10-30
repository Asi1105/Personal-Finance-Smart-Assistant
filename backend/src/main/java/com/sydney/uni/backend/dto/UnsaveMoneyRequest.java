package com.sydney.uni.backend.dto;

import lombok.Data;

@Data
public class UnsaveMoneyRequest {
    private Double amount;
    private String description;
}
