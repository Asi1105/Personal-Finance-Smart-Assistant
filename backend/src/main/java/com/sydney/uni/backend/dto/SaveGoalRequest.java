package com.sydney.uni.backend.dto;

import lombok.Data;

@Data
public class SaveGoalRequest {
    private Double targetAmount;
    private String description;
}
