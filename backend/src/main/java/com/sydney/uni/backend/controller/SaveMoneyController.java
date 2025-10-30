package com.sydney.uni.backend.controller;

import com.sydney.uni.backend.dto.ApiResponse;
import com.sydney.uni.backend.dto.ErrorResponse;
import com.sydney.uni.backend.dto.SaveMoneyRequest;
import com.sydney.uni.backend.entity.Account;
import com.sydney.uni.backend.services.SaveMoneyService;
import com.sydney.uni.backend.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/save-money")
public class SaveMoneyController {

    private static final String INVALID_TOKEN_MESSAGE = "Invalid token";
    private static final String UNAUTHORIZED_MESSAGE = "Authorization token required";
    private static final String UNAUTHORIZED_CODE = "UNAUTHORIZED";
    private static final String INVALID_TOKEN_CODE = "INVALID_TOKEN";

    private final SaveMoneyService saveMoneyService;
    private final JwtUtil jwtUtil;

    public SaveMoneyController(SaveMoneyService saveMoneyService, JwtUtil jwtUtil) {
        this.saveMoneyService = saveMoneyService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Account>> saveMoney(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody SaveMoneyRequest saveMoneyRequest) {
        
        if (token == null || !token.startsWith("Bearer ")) {
            ErrorResponse error = new ErrorResponse(UNAUTHORIZED_MESSAGE, UNAUTHORIZED_CODE, null);
            return new ResponseEntity<>(ApiResponse.fail("Unauthorized", error), HttpStatus.UNAUTHORIZED);
        }

        try {
            String tokenValue = token.substring(7); // Remove "Bearer " prefix
            Long userId = jwtUtil.extractUserId(tokenValue);
            
            if (userId != null) {
                Account account = saveMoneyService.saveMoney(userId, saveMoneyRequest);
                return ResponseEntity.ok(ApiResponse.ok(account));
            }
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Failed to save money: " + e.getMessage(), "SAVE_MONEY_FAILED", null);
            return new ResponseEntity<>(ApiResponse.fail("Save Money Failed", error), HttpStatus.BAD_REQUEST);
        }

        ErrorResponse error = new ErrorResponse(INVALID_TOKEN_MESSAGE, INVALID_TOKEN_CODE, null);
        return new ResponseEntity<>(ApiResponse.fail(INVALID_TOKEN_MESSAGE, error), HttpStatus.UNAUTHORIZED);
    }
}
