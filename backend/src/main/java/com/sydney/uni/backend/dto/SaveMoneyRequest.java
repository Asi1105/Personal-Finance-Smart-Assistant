package com.sydney.uni.backend.dto;

import lombok.Data;

@Data
public class SaveMoneyRequest {
    private Double amount;
    private String description;
}
