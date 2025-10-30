package com.sydney.uni.backend.controller;

import com.sydney.uni.backend.dto.ApiResponse;
import com.sydney.uni.backend.dto.DashboardStatsDto;
import com.sydney.uni.backend.dto.TransactionDto;
import com.sydney.uni.backend.services.DashboardService;
import com.sydney.uni.backend.utils.JwtUtil;
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
class DashboardControllerTest {

    @Mock
    private DashboardService dashboardService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private DashboardController dashboardController;

    private static final String VALID_TOKEN = "Bearer valid.jwt.token";

    @BeforeEach
    void setup() {
        when(jwtUtil.extractUserId(anyString())).thenReturn(1L);
    }

    // Get Dashboard
    @Test
    void testGetDashboardStats_Success() {
        DashboardStatsDto stats = new DashboardStatsDto();
        stats.setTotalBalance(1000.0);
        stats.setSaved(200.0);
        stats.setMonthlySpending(400.0);
        stats.setBudgetLeft(600.0);
        stats.setSavingsGoal(500.0);
        stats.setSavingsProgress(0.8);
        stats.setBudgetUsedPercentage(40.0);
        stats.setMonthlySpendingChange(5.0);
        stats.setLastMonthSpending(380.0);
        stats.setHasSavingsGoal(true);

        when(dashboardService.getDashboardStats(eq(1L))).thenReturn(stats);

        ResponseEntity<ApiResponse<DashboardStatsDto>> response =
                dashboardController.getDashboardStats(VALID_TOKEN);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1000.0, response.getBody().getData().getTotalBalance());
        assertEquals(400.0, response.getBody().getData().getMonthlySpending());
        assertTrue(response.getBody().getData().getHasSavingsGoal());
    }

    // NoToken
    @Test
    void testGetDashboardStats_NoToken() {
        ResponseEntity<ApiResponse<DashboardStatsDto>> response =
                dashboardController.getDashboardStats(null);

        assertEquals(401, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Unauthorized", response.getBody().getMessage());
    }

    //  InvalidToken
    @Test
    void testGetDashboardStats_InvalidToken() {
        when(jwtUtil.extractUserId(anyString())).thenThrow(new RuntimeException("Invalid token"));

        ResponseEntity<ApiResponse<DashboardStatsDto>> response =
                dashboardController.getDashboardStats("Bearer invalid.token");

        assertEquals(401, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Invalid token", response.getBody().getMessage());
    }

    // Success
    @Test
    void testGetRecentTransactions_Success() {
        TransactionDto t1 = new TransactionDto();
        t1.setDetail("Lunch");
        t1.setAmount(15.0);
        t1.setCategoryDisplayName("Food");

        TransactionDto t2 = new TransactionDto();
        t2.setDetail("Transport");
        t2.setAmount(5.0);
        t2.setCategoryDisplayName("Travel");

        when(dashboardService.getRecentTransactions(eq(1L), eq(10)))
                .thenReturn(List.of(t1, t2));

        ResponseEntity<ApiResponse<List<TransactionDto>>> response =
                dashboardController.getRecentTransactions(VALID_TOKEN, 10);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        assertEquals(2, response.getBody().getData().size());
        assertEquals("Lunch", response.getBody().getData().get(0).getDetail());
        assertEquals("Food", response.getBody().getData().get(0).getCategoryDisplayName());
    }

    // NoToken
    @Test
    void testGetRecentTransactions_NoToken() {
        ResponseEntity<ApiResponse<List<TransactionDto>>> response =
                dashboardController.getRecentTransactions(null, 10);

        assertEquals(401, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Unauthorized", response.getBody().getMessage());
    }

    // InvalidToken
    @Test
    void testGetRecentTransactions_InvalidToken() {
        when(jwtUtil.extractUserId(anyString())).thenThrow(new RuntimeException("Invalid token"));

        ResponseEntity<ApiResponse<List<TransactionDto>>> response =
                dashboardController.getRecentTransactions("Bearer invalid.token", 10);

        assertEquals(401, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Invalid token", response.getBody().getMessage());
    }
}
