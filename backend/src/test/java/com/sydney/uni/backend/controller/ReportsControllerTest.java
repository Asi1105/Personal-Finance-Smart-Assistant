package com.sydney.uni.backend.controller;

import com.sydney.uni.backend.dto.ReportsDto;
import com.sydney.uni.backend.dto.ReportsMetricsDto;
import com.sydney.uni.backend.services.ReportsService;
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

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ReportsControllerTest {

    @Mock
    private ReportsService reportsService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private ReportsController reportsController;

    private static final String VALID_TOKEN = "Bearer valid.jwt.token";
    private static final String VALID_TOKEN_VALUE = "valid.jwt.token";
    private static final String INVALID_TOKEN = "Bearer invalid.token";
    private static final String INVALID_TOKEN_VALUE = "invalid.token";
    private static final Long USER_ID = 1L;

    private ReportsDto mockReportsDto;

    @BeforeEach
    void setup() {
        when(jwtUtil.extractUserId(VALID_TOKEN_VALUE)).thenReturn(USER_ID);
        ReportsMetricsDto metrics = new ReportsMetricsDto(1000.0, 500.0, 100.0, 500.0, 0.5);
        mockReportsDto = new ReportsDto(
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                metrics
        );
    }

    @Test
    void testGetReports_Success() {
        String period = "6months";
        when(reportsService.getReportsData(USER_ID, period)).thenReturn(mockReportsDto);
        ResponseEntity<Object> response = reportsController.getReports(period, VALID_TOKEN);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof ReportsDto);
        ReportsDto body = (ReportsDto) response.getBody();
        assertEquals(1000.0, body.getMetrics().getTotalIncome());
        assertEquals(500.0, body.getMetrics().getTotalExpenses());
    }

    @Test
    void testGetReports_Unauthorized_NoToken() {
        String period = "6months";
        ResponseEntity<Object> response = reportsController.getReports(period, null);
        assertEquals(401, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Unauthorized: Missing or invalid token", response.getBody());
    }

    @Test
    void testGetReports_Unauthorized_BadFormat() {
        String period = "6months";
        String badToken = "InvalidFormat";

        ResponseEntity<Object> response = reportsController.getReports(period, badToken);

        assertEquals(401, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Unauthorized: Missing or invalid token", response.getBody());
    }

    @Test
    void testGetReports_InvalidToken_JwtException() {
        String period = "6months";
        when(jwtUtil.extractUserId(INVALID_TOKEN_VALUE)).thenThrow(new JwtException("Invalid signature"));

        ResponseEntity<Object> response = reportsController.getReports(period, INVALID_TOKEN);

        assertEquals(401, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Unauthorized: Invalid token", response.getBody());
    }

    @Test
    void testGetReports_InvalidToken_UserIdNull() {
        String period = "6months";
        when(jwtUtil.extractUserId(VALID_TOKEN_VALUE)).thenReturn(null);

        ResponseEntity<Object> response = reportsController.getReports(period, VALID_TOKEN);

        assertEquals(401, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Unauthorized: Invalid token", response.getBody());
    }

    @Test
    void testGetReports_ServiceFailure() {
        String period = "6months";
        when(reportsService.getReportsData(USER_ID, period)).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<Object> response = reportsController.getReports(period, VALID_TOKEN);

        assertEquals(401, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Unauthorized: Invalid token", response.getBody());
    }
}