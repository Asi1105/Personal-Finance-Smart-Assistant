package com.sydney.uni.backend.controller;

import com.sydney.uni.backend.dto.ApiResponse;
import com.sydney.uni.backend.dto.BudgetRequest;
import com.sydney.uni.backend.dto.BudgetDto;
import com.sydney.uni.backend.dto.ErrorResponse;
import com.sydney.uni.backend.entity.Budget;
import com.sydney.uni.backend.services.BudgetService;
import com.sydney.uni.backend.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private static final String INVALID_TOKEN_MESSAGE = "Invalid token";
    private static final String UNAUTHORIZED_MESSAGE = "Authorization token required";
    private static final String UNAUTHORIZED_CODE = "UNAUTHORIZED";
    private static final String INVALID_TOKEN_CODE = "INVALID_TOKEN";
    private static final String UNAUTHORIZED_TITLE = "Unauthorized";
    private static final String BEARER_PREFIX = "Bearer ";

    private final BudgetService budgetService;
    private final JwtUtil jwtUtil;

    public BudgetController(BudgetService budgetService, JwtUtil jwtUtil) {
        this.budgetService = budgetService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Budget>> addBudget(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody BudgetRequest budgetRequest) {
        
        if (token == null || !token.startsWith(BEARER_PREFIX)) {
            ErrorResponse error = new ErrorResponse(UNAUTHORIZED_MESSAGE, UNAUTHORIZED_CODE, null);
            return new ResponseEntity<>(ApiResponse.fail(UNAUTHORIZED_TITLE, error), HttpStatus.UNAUTHORIZED);
        }

        try {
            String tokenValue = token.substring(7); // Remove "Bearer " prefix
            Long userId = jwtUtil.extractUserId(tokenValue);
            
            if (userId != null) {
                Budget budget = budgetService.addBudget(userId, budgetRequest);
                return ResponseEntity.ok(ApiResponse.ok(budget));
            }
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Failed to add budget: " + e.getMessage(), "BUDGET_FAILED", null);
            return new ResponseEntity<>(ApiResponse.fail("Add Budget Failed", error), HttpStatus.BAD_REQUEST);
        }

        ErrorResponse error = new ErrorResponse(INVALID_TOKEN_MESSAGE, INVALID_TOKEN_CODE, null);
        return new ResponseEntity<>(ApiResponse.fail(INVALID_TOKEN_MESSAGE, error), HttpStatus.UNAUTHORIZED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BudgetDto>>> getUserBudgets(
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        if (token == null || !token.startsWith(BEARER_PREFIX)) {
            ErrorResponse error = new ErrorResponse(UNAUTHORIZED_MESSAGE, UNAUTHORIZED_CODE, null);
            return new ResponseEntity<>(ApiResponse.fail(UNAUTHORIZED_TITLE, error), HttpStatus.UNAUTHORIZED);
        }

        try {
            String tokenValue = token.substring(7); // Remove "Bearer " prefix
            Long userId = jwtUtil.extractUserId(tokenValue);
            
            if (userId != null) {
                List<BudgetDto> budgets = budgetService.getUserBudgetsWithSpending(userId);
                return ResponseEntity.ok(ApiResponse.ok(budgets));
            }
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Failed to get budgets: " + e.getMessage(), "GET_BUDGETS_FAILED", null);
            return new ResponseEntity<>(ApiResponse.fail("Get Budgets Failed", error), HttpStatus.BAD_REQUEST);
        }

        ErrorResponse error = new ErrorResponse(INVALID_TOKEN_MESSAGE, INVALID_TOKEN_CODE, null);
        return new ResponseEntity<>(ApiResponse.fail(INVALID_TOKEN_MESSAGE, error), HttpStatus.UNAUTHORIZED);
    }

    @DeleteMapping("/{budgetId}")
    public ResponseEntity<ApiResponse<String>> deleteBudget(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long budgetId) {
        
        if (token == null || !token.startsWith(BEARER_PREFIX)) {
            ErrorResponse error = new ErrorResponse(UNAUTHORIZED_MESSAGE, UNAUTHORIZED_CODE, null);
            return new ResponseEntity<>(ApiResponse.fail(UNAUTHORIZED_TITLE, error), HttpStatus.UNAUTHORIZED);
        }

        try {
            String tokenValue = token.substring(BEARER_PREFIX.length());
            Long userId = jwtUtil.extractUserId(tokenValue);
            
            if (userId != null) {
                budgetService.deleteBudget(userId, budgetId);
                return ResponseEntity.ok(ApiResponse.ok("Budget deleted successfully"));
            }
        } catch (RuntimeException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage(), "DELETE_BUDGET_FAILED", null);
            return new ResponseEntity<>(ApiResponse.fail("Delete Budget Failed", error), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Token parsing failed or other unexpected error
        }

        ErrorResponse error = new ErrorResponse(INVALID_TOKEN_MESSAGE, INVALID_TOKEN_CODE, null);
        return new ResponseEntity<>(ApiResponse.fail(INVALID_TOKEN_MESSAGE, error), HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/{budgetId}")
    public ResponseEntity<ApiResponse<BudgetDto>> getBudget(
            @PathVariable Long budgetId,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        if (token == null || !token.startsWith(BEARER_PREFIX)) {
            ErrorResponse error = new ErrorResponse(UNAUTHORIZED_MESSAGE, UNAUTHORIZED_CODE, null);
            return new ResponseEntity<>(ApiResponse.fail(UNAUTHORIZED_TITLE, error), HttpStatus.UNAUTHORIZED);
        }

        try {
            String tokenValue = token.substring(7); // Remove "Bearer " prefix
            Long userId = jwtUtil.extractUserId(tokenValue);
            
            if (userId != null) {
                BudgetDto budget = budgetService.getBudgetById(budgetId, userId);
                return ResponseEntity.ok(ApiResponse.ok(budget));
            }
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Failed to get budget: " + e.getMessage(), "GET_BUDGET_FAILED", null);
            return new ResponseEntity<>(ApiResponse.fail("Get Budget Failed", error), HttpStatus.BAD_REQUEST);
        }

        ErrorResponse error = new ErrorResponse(INVALID_TOKEN_MESSAGE, INVALID_TOKEN_CODE, null);
        return new ResponseEntity<>(ApiResponse.fail(INVALID_TOKEN_MESSAGE, error), HttpStatus.UNAUTHORIZED);
    }

    @PutMapping("/{budgetId}")
    public ResponseEntity<ApiResponse<Budget>> updateBudget(
            @PathVariable Long budgetId,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody BudgetRequest budgetRequest) {
        
        if (token == null || !token.startsWith(BEARER_PREFIX)) {
            ErrorResponse error = new ErrorResponse(UNAUTHORIZED_MESSAGE, UNAUTHORIZED_CODE, null);
            return new ResponseEntity<>(ApiResponse.fail(UNAUTHORIZED_TITLE, error), HttpStatus.UNAUTHORIZED);
        }

        try {
            String tokenValue = token.substring(7); // Remove "Bearer " prefix
            Long userId = jwtUtil.extractUserId(tokenValue);
            
            if (userId != null) {
                Budget budget = budgetService.updateBudget(budgetId, userId, budgetRequest);
                return ResponseEntity.ok(ApiResponse.ok(budget));
            }
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Failed to update budget: " + e.getMessage(), "UPDATE_BUDGET_FAILED", null);
            return new ResponseEntity<>(ApiResponse.fail("Update Budget Failed", error), HttpStatus.BAD_REQUEST);
        }

        ErrorResponse error = new ErrorResponse(INVALID_TOKEN_MESSAGE, INVALID_TOKEN_CODE, null);
        return new ResponseEntity<>(ApiResponse.fail(INVALID_TOKEN_MESSAGE, error), HttpStatus.UNAUTHORIZED);
    }
}
