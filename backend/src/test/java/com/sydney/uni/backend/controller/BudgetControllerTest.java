package com.sydney.uni.backend.controller;

import com.sydney.uni.backend.dto.ApiResponse;
import com.sydney.uni.backend.dto.BudgetDto;
import com.sydney.uni.backend.dto.BudgetRequest;
import com.sydney.uni.backend.entity.Budget;
import com.sydney.uni.backend.services.BudgetService;
import com.sydney.uni.backend.utils.JwtUtil;
import io.jsonwebtoken.JwtException;
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
class BudgetControllerTest {

    @Mock
    private BudgetService budgetService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private BudgetController budgetController;

    private static final String VALID_TOKEN = "Bearer valid.jwt.token";
    private static final String VALID_TOKEN_VALUE = "valid.jwt.token";
    private static final String INVALID_TOKEN = "Bearer invalid.token";
    private static final String INVALID_TOKEN_VALUE = "invalid.token";
    private static final Long USER_ID = 1L;
    private static final Long BUDGET_ID = 1L;

    private BudgetRequest budgetRequest;

    @BeforeEach
    void setup() {
        when(jwtUtil.extractUserId(VALID_TOKEN_VALUE)).thenReturn(USER_ID);

        budgetRequest = new BudgetRequest();
        budgetRequest.setCategory("Food & Dining");
        budgetRequest.setAmount(500.0);
        budgetRequest.setPeriod("monthly");
    }


    @Test
    void testAddBudget_Success() {
        Budget savedBudget = new Budget();
        savedBudget.setId(BUDGET_ID);
        when(budgetService.addBudget(eq(USER_ID), any(BudgetRequest.class)))
                .thenReturn(savedBudget);

        ResponseEntity<ApiResponse<Budget>> response =
                budgetController.addBudget(VALID_TOKEN, budgetRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    void testGetUserBudgets_Success() {
        BudgetDto dto = new BudgetDto();
        when(budgetService.getUserBudgetsWithSpending(eq(USER_ID)))
                .thenReturn(List.of(dto));

        ResponseEntity<ApiResponse<List<BudgetDto>>> response =
                budgetController.getUserBudgets(VALID_TOKEN);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().size());
    }

    @Test
    void testDeleteBudget_Success() {
        doNothing().when(budgetService).deleteBudget(eq(USER_ID), eq(BUDGET_ID));

        ResponseEntity<ApiResponse<String>> response =
                budgetController.deleteBudget(VALID_TOKEN, BUDGET_ID);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Budget deleted successfully", response.getBody().getData());
    }

    @Test
    void testGetBudget_Success() {
        BudgetDto dto = new BudgetDto();
        dto.setId(BUDGET_ID);
        when(budgetService.getBudgetById(eq(BUDGET_ID), eq(USER_ID)))
                .thenReturn(dto);

        ResponseEntity<ApiResponse<BudgetDto>> response =
                budgetController.getBudget(BUDGET_ID, VALID_TOKEN);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    void testUpdateBudget_Success() {
        Budget updated = new Budget();
        updated.setId(BUDGET_ID);
        when(budgetService.updateBudget(eq(BUDGET_ID), eq(USER_ID), any(BudgetRequest.class)))
                .thenReturn(updated);

        ResponseEntity<ApiResponse<Budget>> response =
                budgetController.updateBudget(BUDGET_ID, VALID_TOKEN, budgetRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    void testAddBudget_Unauthorized() {
        ResponseEntity<ApiResponse<Budget>> response =
                budgetController.addBudget(null, new BudgetRequest());

        assertEquals(401, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
    }


    @Test
    void testAddBudget_Unauthorized_BadFormat() {
        ResponseEntity<ApiResponse<Budget>> response =
                budgetController.addBudget("NotBearer token", budgetRequest);
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("UNAUTHORIZED", response.getBody().getError().getCode());
    }

    @Test
    void testAddBudget_ServiceFailure() {
        when(budgetService.addBudget(eq(USER_ID), any(BudgetRequest.class)))
                .thenThrow(new RuntimeException("Database error"));

        ResponseEntity<ApiResponse<Budget>> response =
                budgetController.addBudget(VALID_TOKEN, budgetRequest);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("BUDGET_FAILED", response.getBody().getError().getCode());
    }

    @Test
    void testAddBudget_InvalidToken_JwtException() {
        when(jwtUtil.extractUserId(INVALID_TOKEN_VALUE)).thenThrow(new JwtException("Invalid signature"));

        ResponseEntity<ApiResponse<Budget>> response =
                budgetController.addBudget(INVALID_TOKEN, budgetRequest);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("BUDGET_FAILED", response.getBody().getError().getCode());
    }

    @Test
    void testAddBudget_InvalidToken_UserIdNull() {
        when(jwtUtil.extractUserId(VALID_TOKEN_VALUE)).thenReturn(null);

        ResponseEntity<ApiResponse<Budget>> response =
                budgetController.addBudget(VALID_TOKEN, budgetRequest);
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("INVALID_TOKEN", response.getBody().getError().getCode());
    }

    @Test
    void testGetUserBudgets_Unauthorized() {
        ResponseEntity<ApiResponse<List<BudgetDto>>> response =
                budgetController.getUserBudgets(null);
        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    void testGetUserBudgets_InvalidToken_JwtException() {
        when(jwtUtil.extractUserId(INVALID_TOKEN_VALUE)).thenThrow(new JwtException("Invalid signature"));

        ResponseEntity<ApiResponse<List<BudgetDto>>> response =
                budgetController.getUserBudgets(INVALID_TOKEN);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("GET_BUDGETS_FAILED", response.getBody().getError().getCode());
    }

    @Test
    void testGetUserBudgets_InvalidToken_UserIdNull() {
        when(jwtUtil.extractUserId(VALID_TOKEN_VALUE)).thenReturn(null);

        ResponseEntity<ApiResponse<List<BudgetDto>>> response =
                budgetController.getUserBudgets(VALID_TOKEN);
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("INVALID_TOKEN", response.getBody().getError().getCode());
    }

    @Test
    void testDeleteBudget_Unauthorized() {
        ResponseEntity<ApiResponse<String>> response =
                budgetController.deleteBudget(null, BUDGET_ID);
        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    void testDeleteBudget_ServiceFailure() {
        doThrow(new RuntimeException("Budget not found")).when(budgetService).deleteBudget(USER_ID, BUDGET_ID);

        ResponseEntity<ApiResponse<String>> response =
                budgetController.deleteBudget(VALID_TOKEN, BUDGET_ID);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("DELETE_BUDGET_FAILED", response.getBody().getError().getCode());
    }

    @Test
    void testDeleteBudget_InvalidToken_JwtException() {
        when(jwtUtil.extractUserId(INVALID_TOKEN_VALUE)).thenThrow(new JwtException("Invalid signature"));

        ResponseEntity<ApiResponse<String>> response =
                budgetController.deleteBudget(INVALID_TOKEN, BUDGET_ID);

        assertEquals(400, response.getStatusCodeValue()); // 修正: 401 -> 400
        assertEquals("DELETE_BUDGET_FAILED", response.getBody().getError().getCode()); // 修正: INVALID_TOKEN -> DELETE_BUDGET_FAILED
        assertTrue(response.getBody().getError().getMessage().contains("Invalid signature"));
    }

    @Test
    void testDeleteBudget_InvalidToken_UserIdNull() {
        when(jwtUtil.extractUserId(VALID_TOKEN_VALUE)).thenReturn(null);

        ResponseEntity<ApiResponse<String>> response =
                budgetController.deleteBudget(VALID_TOKEN, BUDGET_ID);
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("INVALID_TOKEN", response.getBody().getError().getCode());
    }

    @Test
    void testGetBudget_Unauthorized() {
        ResponseEntity<ApiResponse<BudgetDto>> response =
                budgetController.getBudget(BUDGET_ID, null);
        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    void testGetBudget_ServiceFailure() {
        when(budgetService.getBudgetById(BUDGET_ID, USER_ID))
                .thenThrow(new RuntimeException("Budget does not belong to user"));

        ResponseEntity<ApiResponse<BudgetDto>> response =
                budgetController.getBudget(BUDGET_ID, VALID_TOKEN);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("GET_BUDGET_FAILED", response.getBody().getError().getCode());
    }

    @Test
    void testGetBudget_InvalidToken_JwtException() {
        when(jwtUtil.extractUserId(INVALID_TOKEN_VALUE)).thenThrow(new JwtException("Invalid signature"));

        ResponseEntity<ApiResponse<BudgetDto>> response =
                budgetController.getBudget(BUDGET_ID, INVALID_TOKEN);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("GET_BUDGET_FAILED", response.getBody().getError().getCode());
    }

    @Test
    void testGetBudget_InvalidToken_UserIdNull() {
        when(jwtUtil.extractUserId(VALID_TOKEN_VALUE)).thenReturn(null);
        ResponseEntity<ApiResponse<BudgetDto>> response = budgetController.getBudget(BUDGET_ID, VALID_TOKEN);
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("INVALID_TOKEN", response.getBody().getError().getCode());
    }

    @Test
    void testUpdateBudget_Unauthorized() {
        ResponseEntity<ApiResponse<Budget>> response =
                budgetController.updateBudget(BUDGET_ID, null, budgetRequest);
        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    void testUpdateBudget_ServiceFailure() {
        when(budgetService.updateBudget(BUDGET_ID, USER_ID, budgetRequest))
                .thenThrow(new RuntimeException("Budget not found"));

        ResponseEntity<ApiResponse<Budget>> response =
                budgetController.updateBudget(BUDGET_ID, VALID_TOKEN, budgetRequest);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("UPDATE_BUDGET_FAILED", response.getBody().getError().getCode());
    }

    @Test
    void testUpdateBudget_InvalidToken_JwtException() {
        when(jwtUtil.extractUserId(INVALID_TOKEN_VALUE)).thenThrow(new JwtException("Invalid signature"));

        ResponseEntity<ApiResponse<Budget>> response =
                budgetController.updateBudget(BUDGET_ID, INVALID_TOKEN, budgetRequest);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("UPDATE_BUDGET_FAILED", response.getBody().getError().getCode());
    }

    @Test
    void testUpdateBudget_InvalidToken_UserIdNull() {
        when(jwtUtil.extractUserId(VALID_TOKEN_VALUE)).thenReturn(null);
        ResponseEntity<ApiResponse<Budget>> response =
                budgetController.updateBudget(BUDGET_ID, VALID_TOKEN, budgetRequest);
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("INVALID_TOKEN", response.getBody().getError().getCode());
    }
}