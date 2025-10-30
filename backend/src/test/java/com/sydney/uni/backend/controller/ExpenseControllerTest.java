package com.sydney.uni.backend.controller;

import com.sydney.uni.backend.dto.ApiResponse;
import com.sydney.uni.backend.dto.ExpenseRequest;
import com.sydney.uni.backend.dto.TransactionDto;
import com.sydney.uni.backend.entity.Transaction;
import com.sydney.uni.backend.services.DashboardService;
import com.sydney.uni.backend.services.ExpenseService;
import com.sydney.uni.backend.utils.JwtUtil;
import io.jsonwebtoken.JwtException; // 确保你 import 了这个
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ExpenseControllerTest {

    @Mock
    private ExpenseService expenseService;

    @Mock
    private DashboardService dashboardService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private ExpenseController expenseController;

    private static final String VALID_TOKEN = "Bearer valid.jwt.token";
    private static final String VALID_TOKEN_VALUE = "valid.jwt.token";
    private static final String INVALID_TOKEN = "Bearer invalid.token";
    private static final String INVALID_TOKEN_VALUE = "invalid.token";
    private static final Long USER_ID = 1L;

    private ExpenseRequest expenseRequest;

    @BeforeEach
    void setup() {
        // 模拟特定的 Token 行为
        when(jwtUtil.extractUserId(VALID_TOKEN_VALUE)).thenReturn(USER_ID);
        when(jwtUtil.extractUserId(INVALID_TOKEN_VALUE)).thenThrow(new JwtException("Invalid token"));

        expenseRequest = new ExpenseRequest();
        expenseRequest.setCategory("Food");
        expenseRequest.setAmount(50.0);
    }


    @Test
    void testAddExpense_Success() {
        Transaction transaction = new Transaction();
        TransactionDto dto = new TransactionDto();

        when(expenseService.addExpense(eq(USER_ID), any(ExpenseRequest.class))).thenReturn(transaction);
        when(dashboardService.convertToTransactionDto(transaction)).thenReturn(dto);

        ResponseEntity<ApiResponse<TransactionDto>> response =
                expenseController.addExpense(VALID_TOKEN, expenseRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        assertNotNull(response.getBody().getData());
    }

    @Test
    void testAddExpense_NoToken() {
        ResponseEntity<ApiResponse<TransactionDto>> response =
                expenseController.addExpense(null, expenseRequest);

        assertEquals(401, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    void testAddExpense_InvalidToken_JwtException() {
        ResponseEntity<ApiResponse<TransactionDto>> response =
                expenseController.addExpense(INVALID_TOKEN, expenseRequest);

        assertEquals(400, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("EXPENSE_FAILED", response.getBody().getError().getCode());
    }

    @Test
    void testGetUserExpenses_Success() {
        Transaction transaction = new Transaction();
        TransactionDto dto = new TransactionDto();

        when(expenseService.getUserExpenses(eq(USER_ID))).thenReturn(List.of(transaction));
        when(dashboardService.convertToTransactionDto(transaction)).thenReturn(dto);

        ResponseEntity<ApiResponse<List<TransactionDto>>> response =
                expenseController.getUserExpenses(VALID_TOKEN);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        assertNotNull(response.getBody().getData());
    }

    @Test
    void testGetUserExpenses_NoToken() {
        ResponseEntity<ApiResponse<List<TransactionDto>>> response =
                expenseController.getUserExpenses(null);

        assertEquals(401, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    void testGetExpense_Success() {
        Transaction transaction = new Transaction();
        TransactionDto dto = new TransactionDto();

        when(expenseService.getExpenseById(eq(5L), eq(USER_ID))).thenReturn(transaction);
        when(dashboardService.convertToTransactionDto(transaction)).thenReturn(dto);

        ResponseEntity<ApiResponse<TransactionDto>> response =
                expenseController.getExpense(5L, VALID_TOKEN);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    void testUpdateExpense_Success() {
        Transaction transaction = new Transaction();
        TransactionDto dto = new TransactionDto();

        when(expenseService.updateExpense(eq(2L), eq(USER_ID), any(ExpenseRequest.class))).thenReturn(transaction);
        when(dashboardService.convertToTransactionDto(transaction)).thenReturn(dto);

        ResponseEntity<ApiResponse<TransactionDto>> response =
                expenseController.updateExpense(2L, VALID_TOKEN, expenseRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    void testDeleteExpense_Success() {
        doNothing().when(expenseService).deleteExpense(3L, USER_ID);

        ResponseEntity<ApiResponse<Void>> response =
                expenseController.deleteExpense(3L, VALID_TOKEN);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    void testDeleteExpense_NoToken() {
        ResponseEntity<ApiResponse<Void>> response =
                expenseController.deleteExpense(3L, null);

        assertEquals(401, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    void testDeleteExpense_InvalidToken_JwtException() {
        // 这个测试覆盖了 catch (Exception e)
        ResponseEntity<ApiResponse<Void>> response =
                expenseController.deleteExpense(3L, INVALID_TOKEN);

        assertEquals(400, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("DELETE_EXPENSE_FAILED", response.getBody().getError().getCode());
    }


    @Test
    void testAddExpense_ServiceFailure() {
        when(expenseService.addExpense(eq(USER_ID), any(ExpenseRequest.class)))
                .thenThrow(new RuntimeException("Insufficient balance"));

        ResponseEntity<ApiResponse<TransactionDto>> response =
                expenseController.addExpense(VALID_TOKEN, expenseRequest);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("EXPENSE_FAILED", response.getBody().getError().getCode());
        assertTrue(response.getBody().getError().getMessage().contains("Insufficient balance"));
    }

    @Test
    void testGetUserExpenses_ServiceFailure() {
        when(expenseService.getUserExpenses(USER_ID))
                .thenThrow(new RuntimeException("Database error"));

        ResponseEntity<ApiResponse<List<TransactionDto>>> response =
                expenseController.getUserExpenses(VALID_TOKEN);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("GET_EXPENSES_FAILED", response.getBody().getError().getCode());
    }

    @Test
    void testGetExpense_ServiceFailure() {
        when(expenseService.getExpenseById(5L, USER_ID))
                .thenThrow(new RuntimeException("Expense not found"));

        ResponseEntity<ApiResponse<TransactionDto>> response =
                expenseController.getExpense(5L, VALID_TOKEN);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("GET_EXPENSE_FAILED", response.getBody().getError().getCode());
    }

    @Test
    void testUpdateExpense_ServiceFailure() {
        when(expenseService.updateExpense(eq(2L), eq(USER_ID), any(ExpenseRequest.class)))
                .thenThrow(new RuntimeException("Expense does not belong to user"));

        ResponseEntity<ApiResponse<TransactionDto>> response =
                expenseController.updateExpense(2L, VALID_TOKEN, expenseRequest);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("UPDATE_EXPENSE_FAILED", response.getBody().getError().getCode());
    }

    @Test
    void testDeleteExpense_ServiceFailure() {
        doThrow(new RuntimeException("Expense not found")).when(expenseService).deleteExpense(3L, USER_ID);

        ResponseEntity<ApiResponse<Void>> response =
                expenseController.deleteExpense(3L, VALID_TOKEN);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("DELETE_EXPENSE_FAILED", response.getBody().getError().getCode());
    }


    @Test
    void testAddExpense_BadFormat() {
        ResponseEntity<ApiResponse<TransactionDto>> response =
                expenseController.addExpense("NotBearer", expenseRequest);
        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    void testGetUserExpenses_BadFormat() {
        ResponseEntity<ApiResponse<List<TransactionDto>>> response =
                expenseController.getUserExpenses("NotBearer");
        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    void testGetExpense_BadFormat() {
        ResponseEntity<ApiResponse<TransactionDto>> response =
                expenseController.getExpense(5L, "NotBearer");
        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    void testUpdateExpense_BadFormat() {
        ResponseEntity<ApiResponse<TransactionDto>> response =
                expenseController.updateExpense(2L, "NotBearer", expenseRequest);
        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    void testDeleteExpense_BadFormat() {
        ResponseEntity<ApiResponse<Void>> response =
                expenseController.deleteExpense(3L, "NotBearer");
        assertEquals(401, response.getStatusCodeValue());
    }


    @Test
    void testAddExpense_UserIdNull() {
        when(jwtUtil.extractUserId(VALID_TOKEN_VALUE)).thenReturn(null);
        ResponseEntity<ApiResponse<TransactionDto>> response =
                expenseController.addExpense(VALID_TOKEN, expenseRequest);
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("INVALID_TOKEN", response.getBody().getError().getCode());
    }

    @Test
    void testGetUserExpenses_UserIdNull() {
        when(jwtUtil.extractUserId(VALID_TOKEN_VALUE)).thenReturn(null);
        ResponseEntity<ApiResponse<List<TransactionDto>>> response =
                expenseController.getUserExpenses(VALID_TOKEN);
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("INVALID_TOKEN", response.getBody().getError().getCode());
    }

    @Test
    void testGetExpense_UserIdNull() {
        when(jwtUtil.extractUserId(VALID_TOKEN_VALUE)).thenReturn(null);
        ResponseEntity<ApiResponse<TransactionDto>> response =
                expenseController.getExpense(5L, VALID_TOKEN);
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("INVALID_TOKEN", response.getBody().getError().getCode());
    }

    @Test
    void testUpdateExpense_UserIdNull() {
        when(jwtUtil.extractUserId(VALID_TOKEN_VALUE)).thenReturn(null);
        ResponseEntity<ApiResponse<TransactionDto>> response =
                expenseController.updateExpense(2L, VALID_TOKEN, expenseRequest);
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("INVALID_TOKEN", response.getBody().getError().getCode());
    }

    @Test
    void testDeleteExpense_UserIdNull() {
        when(jwtUtil.extractUserId(VALID_TOKEN_VALUE)).thenReturn(null);
        ResponseEntity<ApiResponse<Void>> response =
                expenseController.deleteExpense(3L, VALID_TOKEN);
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("INVALID_TOKEN", response.getBody().getError().getCode());
    }


    @Test
    void testGetExpense_NoToken() {
        ResponseEntity<ApiResponse<TransactionDto>> response =
                expenseController.getExpense(5L, null);
        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    void testUpdateExpense_NoToken() {
        ResponseEntity<ApiResponse<TransactionDto>> response =
                expenseController.updateExpense(2L, null, expenseRequest);
        assertEquals(401, response.getStatusCodeValue());
    }
}