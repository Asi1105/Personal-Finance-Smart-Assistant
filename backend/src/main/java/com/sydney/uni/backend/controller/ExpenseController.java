package com.sydney.uni.backend.controller;

import com.sydney.uni.backend.dto.ApiResponse;
import com.sydney.uni.backend.dto.ErrorResponse;
import com.sydney.uni.backend.dto.ExpenseRequest;
import com.sydney.uni.backend.dto.TransactionDto;
import com.sydney.uni.backend.entity.Transaction;
import com.sydney.uni.backend.services.DashboardService;
import com.sydney.uni.backend.services.ExpenseService;
import com.sydney.uni.backend.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
    
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String UNAUTHORIZED_MESSAGE = "Unauthorized";
    private static final String UNAUTHORIZED_CODE = "UNAUTHORIZED";
    private static final String INVALID_TOKEN_MESSAGE = "Invalid token";
    private static final String INVALID_TOKEN_CODE = "INVALID_TOKEN";

    private final ExpenseService expenseService;
    private final DashboardService dashboardService;
    private final JwtUtil jwtUtil;

    public ExpenseController(ExpenseService expenseService, DashboardService dashboardService, JwtUtil jwtUtil) {
        this.expenseService = expenseService;
        this.dashboardService = dashboardService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TransactionDto>> addExpense(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody ExpenseRequest expenseRequest) {
        
        if (token == null || !token.startsWith(BEARER_PREFIX)) {
            ErrorResponse error = new ErrorResponse(UNAUTHORIZED_MESSAGE, UNAUTHORIZED_CODE, null);
            return new ResponseEntity<>(ApiResponse.fail(UNAUTHORIZED_MESSAGE, error), HttpStatus.UNAUTHORIZED);
        }

        try {
            String tokenValue = token.substring(BEARER_PREFIX.length()); // Remove "Bearer " prefix
            Long userId = jwtUtil.extractUserId(tokenValue);
            
            if (userId != null) {
                Transaction transaction = expenseService.addExpense(userId, expenseRequest);
                TransactionDto transactionDto = dashboardService.convertToTransactionDto(transaction);
                return ResponseEntity.ok(ApiResponse.ok(transactionDto));
            }
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Failed to add expense: " + e.getMessage(), "EXPENSE_FAILED", null);
            return new ResponseEntity<>(ApiResponse.fail("Add Expense Failed", error), HttpStatus.BAD_REQUEST);
        }

        ErrorResponse error = new ErrorResponse(INVALID_TOKEN_MESSAGE, INVALID_TOKEN_CODE, null);
        return new ResponseEntity<>(ApiResponse.fail(INVALID_TOKEN_MESSAGE, error), HttpStatus.UNAUTHORIZED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TransactionDto>>> getUserExpenses(
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        if (token == null || !token.startsWith(BEARER_PREFIX)) {
            ErrorResponse error = new ErrorResponse(UNAUTHORIZED_MESSAGE, UNAUTHORIZED_CODE, null);
            return new ResponseEntity<>(ApiResponse.fail(UNAUTHORIZED_MESSAGE, error), HttpStatus.UNAUTHORIZED);
        }

        try {
            String tokenValue = token.substring(BEARER_PREFIX.length()); // Remove "Bearer " prefix
            Long userId = jwtUtil.extractUserId(tokenValue);
            
            if (userId != null) {
                List<Transaction> transactions = expenseService.getUserExpenses(userId);
                List<TransactionDto> transactionDtos = transactions.stream()
                    .map(dashboardService::convertToTransactionDto)
                    .toList();
                return ResponseEntity.ok(ApiResponse.ok(transactionDtos));
            }
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Failed to get expenses: " + e.getMessage(), "GET_EXPENSES_FAILED", null);
            return new ResponseEntity<>(ApiResponse.fail("Get Expenses Failed", error), HttpStatus.BAD_REQUEST);
        }

        ErrorResponse error = new ErrorResponse(INVALID_TOKEN_MESSAGE, INVALID_TOKEN_CODE, null);
        return new ResponseEntity<>(ApiResponse.fail(INVALID_TOKEN_MESSAGE, error), HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionDto>> getExpense(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        if (token == null || !token.startsWith(BEARER_PREFIX)) {
            ErrorResponse error = new ErrorResponse(UNAUTHORIZED_MESSAGE, UNAUTHORIZED_CODE, null);
            return new ResponseEntity<>(ApiResponse.fail(UNAUTHORIZED_MESSAGE, error), HttpStatus.UNAUTHORIZED);
        }

        try {
            String tokenValue = token.substring(BEARER_PREFIX.length()); // Remove "Bearer " prefix
            Long userId = jwtUtil.extractUserId(tokenValue);
            
            if (userId != null) {
                Transaction transaction = expenseService.getExpenseById(id, userId);
                TransactionDto transactionDto = dashboardService.convertToTransactionDto(transaction);
                return ResponseEntity.ok(ApiResponse.ok(transactionDto));
            }
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Failed to get expense: " + e.getMessage(), "GET_EXPENSE_FAILED", null);
            return new ResponseEntity<>(ApiResponse.fail("Get Expense Failed", error), HttpStatus.BAD_REQUEST);
        }

        ErrorResponse error = new ErrorResponse(INVALID_TOKEN_MESSAGE, INVALID_TOKEN_CODE, null);
        return new ResponseEntity<>(ApiResponse.fail(INVALID_TOKEN_MESSAGE, error), HttpStatus.UNAUTHORIZED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionDto>> updateExpense(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody ExpenseRequest expenseRequest) {
        
        if (token == null || !token.startsWith(BEARER_PREFIX)) {
            ErrorResponse error = new ErrorResponse(UNAUTHORIZED_MESSAGE, UNAUTHORIZED_CODE, null);
            return new ResponseEntity<>(ApiResponse.fail(UNAUTHORIZED_MESSAGE, error), HttpStatus.UNAUTHORIZED);
        }

        try {
            String tokenValue = token.substring(BEARER_PREFIX.length()); // Remove "Bearer " prefix
            Long userId = jwtUtil.extractUserId(tokenValue);
            
            if (userId != null) {
                Transaction transaction = expenseService.updateExpense(id, userId, expenseRequest);
                TransactionDto transactionDto = dashboardService.convertToTransactionDto(transaction);
                return ResponseEntity.ok(ApiResponse.ok(transactionDto));
            }
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Failed to update expense: " + e.getMessage(), "UPDATE_EXPENSE_FAILED", null);
            return new ResponseEntity<>(ApiResponse.fail("Update Expense Failed", error), HttpStatus.BAD_REQUEST);
        }

        ErrorResponse error = new ErrorResponse(INVALID_TOKEN_MESSAGE, INVALID_TOKEN_CODE, null);
        return new ResponseEntity<>(ApiResponse.fail(INVALID_TOKEN_MESSAGE, error), HttpStatus.UNAUTHORIZED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        if (token == null || !token.startsWith(BEARER_PREFIX)) {
            ErrorResponse error = new ErrorResponse(UNAUTHORIZED_MESSAGE, UNAUTHORIZED_CODE, null);
            return new ResponseEntity<>(ApiResponse.fail(UNAUTHORIZED_MESSAGE, error), HttpStatus.UNAUTHORIZED);
        }

        try {
            String tokenValue = token.substring(BEARER_PREFIX.length()); // Remove "Bearer " prefix
            Long userId = jwtUtil.extractUserId(tokenValue);
            
            if (userId != null) {
                expenseService.deleteExpense(id, userId);
                return ResponseEntity.ok(ApiResponse.ok(null));
            }
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Failed to delete expense: " + e.getMessage(), "DELETE_EXPENSE_FAILED", null);
            return new ResponseEntity<>(ApiResponse.fail("Delete Expense Failed", error), HttpStatus.BAD_REQUEST);
        }

        ErrorResponse error = new ErrorResponse(INVALID_TOKEN_MESSAGE, INVALID_TOKEN_CODE, null);
        return new ResponseEntity<>(ApiResponse.fail(INVALID_TOKEN_MESSAGE, error), HttpStatus.UNAUTHORIZED);
    }
}
