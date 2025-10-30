package com.sydney.uni.backend.dto;

import com.sydney.uni.backend.entity.SavingAction;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SavingLogDto {
    private Long id;
    private SavingAction action;
    private Double amount;
    private String description;
    private LocalDateTime timestamp;
    private String actionDisplayName; // For frontend display
    private String icon; // For frontend display
}
