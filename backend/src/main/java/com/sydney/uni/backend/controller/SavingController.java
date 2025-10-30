package com.sydney.uni.backend.controller;

import com.sydney.uni.backend.dto.ApiResponse;
import com.sydney.uni.backend.dto.ErrorResponse;
import com.sydney.uni.backend.dto.SaveMoneyRequest;
import com.sydney.uni.backend.dto.SavingLogDto;
import com.sydney.uni.backend.dto.UnsaveMoneyRequest;
import com.sydney.uni.backend.entity.Account;
import com.sydney.uni.backend.entity.SavingAction;
import com.sydney.uni.backend.entity.SavingLog;
import com.sydney.uni.backend.services.SaveMoneyService;
import com.sydney.uni.backend.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/saving")
public class SavingController {

    private static final String INVALID_TOKEN_MESSAGE = "Invalid token";
    private static final String UNAUTHORIZED_MESSAGE = "Authorization token required";
    private static final String UNAUTHORIZED_CODE = "UNAUTHORIZED";
    private static final String INVALID_TOKEN_CODE = "INVALID_TOKEN";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String UNAUTHORIZED_TITLE = "Unauthorized";

    private final SaveMoneyService saveMoneyService;
    private final JwtUtil jwtUtil;

    public SavingController(SaveMoneyService saveMoneyService, JwtUtil jwtUtil) {
        this.saveMoneyService = saveMoneyService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/save")
    public ResponseEntity<ApiResponse<Account>> saveMoney(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody SaveMoneyRequest saveMoneyRequest) {

        if (token == null || !token.startsWith(BEARER_PREFIX)) {
            ErrorResponse error = new ErrorResponse(UNAUTHORIZED_MESSAGE, UNAUTHORIZED_CODE, null);
            return new ResponseEntity<>(ApiResponse.fail(UNAUTHORIZED_TITLE, error), HttpStatus.UNAUTHORIZED);
        }

        try {
            String tokenValue = token.substring(BEARER_PREFIX.length()); // Remove "Bearer " prefix
            Long userId = jwtUtil.extractUserId(tokenValue);

            if (userId != null) {
                Account updatedAccount = saveMoneyService.saveMoney(userId, saveMoneyRequest);
                return ResponseEntity.ok(ApiResponse.ok(updatedAccount));
            }
        } catch (RuntimeException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage(), "SAVE_MONEY_FAILED", null);
            return new ResponseEntity<>(ApiResponse.fail("Save Money Failed", error), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Token parsing failed or other unexpected error
        }

        ErrorResponse error = new ErrorResponse(INVALID_TOKEN_MESSAGE, INVALID_TOKEN_CODE, null);
        return new ResponseEntity<>(ApiResponse.fail(INVALID_TOKEN_MESSAGE, error), HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/unsave")
    public ResponseEntity<ApiResponse<Account>> unsaveMoney(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody UnsaveMoneyRequest unsaveMoneyRequest) {

        if (token == null || !token.startsWith(BEARER_PREFIX)) {
            ErrorResponse error = new ErrorResponse(UNAUTHORIZED_MESSAGE, UNAUTHORIZED_CODE, null);
            return new ResponseEntity<>(ApiResponse.fail(UNAUTHORIZED_TITLE, error), HttpStatus.UNAUTHORIZED);
        }

        try {
            String tokenValue = token.substring(BEARER_PREFIX.length()); // Remove "Bearer " prefix
            Long userId = jwtUtil.extractUserId(tokenValue);

            if (userId != null) {
                Account updatedAccount = saveMoneyService.unsaveMoney(userId, unsaveMoneyRequest);
                return ResponseEntity.ok(ApiResponse.ok(updatedAccount));
            }
        } catch (RuntimeException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage(), "UNSAVE_MONEY_FAILED", null);
            return new ResponseEntity<>(ApiResponse.fail("Unsave Money Failed", error), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Token parsing failed or other unexpected error
        }

        ErrorResponse error = new ErrorResponse(INVALID_TOKEN_MESSAGE, INVALID_TOKEN_CODE, null);
        return new ResponseEntity<>(ApiResponse.fail(INVALID_TOKEN_MESSAGE, error), HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/logs")
    public ResponseEntity<ApiResponse<List<SavingLogDto>>> getSavingLogs(
            @RequestHeader(value = "Authorization", required = false) String token) {

        if (token == null || !token.startsWith(BEARER_PREFIX)) {
            ErrorResponse error = new ErrorResponse(UNAUTHORIZED_MESSAGE, UNAUTHORIZED_CODE, null);
            return new ResponseEntity<>(ApiResponse.fail(UNAUTHORIZED_TITLE, error), HttpStatus.UNAUTHORIZED);
        }

        try {
            String tokenValue = token.substring(BEARER_PREFIX.length()); // Remove "Bearer " prefix
            Long userId = jwtUtil.extractUserId(tokenValue);

            if (userId != null) {
                List<SavingLog> savingLogs = saveMoneyService.getSavingLogs(userId);
                List<SavingLogDto> savingLogDtos = savingLogs.stream()
                    .map(this::convertToSavingLogDto)
                    .toList();
                return ResponseEntity.ok(ApiResponse.ok(savingLogDtos));
            }
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Failed to get saving logs: " + e.getMessage(), "GET_SAVING_LOGS_FAILED", null);
            return new ResponseEntity<>(ApiResponse.fail("Get Saving Logs Failed", error), HttpStatus.BAD_REQUEST);
        }

        ErrorResponse error = new ErrorResponse(INVALID_TOKEN_MESSAGE, INVALID_TOKEN_CODE, null);
        return new ResponseEntity<>(ApiResponse.fail(INVALID_TOKEN_MESSAGE, error), HttpStatus.UNAUTHORIZED);
    }

    private SavingLogDto convertToSavingLogDto(SavingLog savingLog) {
        SavingLogDto dto = new SavingLogDto();
        dto.setId(savingLog.getId());
        dto.setAction(savingLog.getAction());
        dto.setAmount(savingLog.getAmount());
        dto.setDescription(savingLog.getDescription());
        dto.setTimestamp(savingLog.getTimestamp());
        
        // Set display name and icon based on action
        if (savingLog.getAction() == SavingAction.SAVE) {
            dto.setActionDisplayName("Money Saved");
            dto.setIcon("💰");
        } else {
            dto.setActionDisplayName("Money Unmarked");
            dto.setIcon("💸");
        }
        
        return dto;
    }
}
