package com.sydney.uni.backend.controller;

import com.sydney.uni.backend.dto.ApiResponse;
import com.sydney.uni.backend.dto.DepositRequest;
import com.sydney.uni.backend.entity.Account;
import com.sydney.uni.backend.services.DepositService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DepositControllerTest {

    @Mock
    private DepositService depositService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private DepositController depositController;

    private static final String VALID_TOKEN = "Bearer valid.jwt.token";
    private static final String VALID_TOKEN_VALUE = "valid.jwt.token";

    @BeforeEach
    void setup() {
        when(jwtUtil.extractUserId(anyString())).thenReturn(1L);
    }

    @Test
    void testProcessDeposit_Success() {
        DepositRequest request = new DepositRequest();
        request.setAmount(200.0);
        request.setDescription("Weekly top-up");

        Account account = new Account();
        account.setId(1L);
        account.setBalance(1200.0);
        account.setSaved(500.0);

        when(depositService.processDeposit(eq(1L), any(DepositRequest.class))).thenReturn(account);

        ResponseEntity<ApiResponse<Account>> response =
                depositController.processDeposit(VALID_TOKEN, request);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        assertNotNull(response.getBody().getData());
        assertEquals(1200.0, response.getBody().getData().getBalance());
        verify(depositService, times(1)).processDeposit(eq(1L), any(DepositRequest.class));
    }

    @Test
    void testProcessDeposit_NoToken() {
        DepositRequest request = new DepositRequest();
        request.setAmount(100.0);

        ResponseEntity<ApiResponse<Account>> response =
                depositController.processDeposit(null, request);

        assertEquals(401, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Unauthorized", response.getBody().getMessage());
        verify(depositService, never()).processDeposit(anyLong(), any());
    }

    @Test
    void testProcessDeposit_InvalidToken() {
        when(jwtUtil.extractUserId(anyString())).thenThrow(new RuntimeException("Invalid token"));

        DepositRequest request = new DepositRequest();
        request.setAmount(300.0);

        ResponseEntity<ApiResponse<Account>> response =
                depositController.processDeposit("Bearer invalid.token", request);

        assertEquals(400, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertTrue(response.getBody().getMessage().contains("Deposit Failed"));
    }

    @Test
    void testProcessDeposit_HeaderWithoutBearer_Unauthorized() {
        DepositRequest request = new DepositRequest();
        ResponseEntity<ApiResponse<Account>> response = depositController.processDeposit("Token x", request);
        assertEquals(401, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    void testProcessDeposit_UserIdNull_Unauthorized() {
        DepositRequest request = new DepositRequest();
        when(jwtUtil.extractUserId(VALID_TOKEN_VALUE)).thenReturn(null);
        // use specific stubbing for exact token value
        ResponseEntity<ApiResponse<Account>> response = depositController.processDeposit(VALID_TOKEN, request);
        assertEquals(401, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    void testProcessDeposit_DepositFailed() {
        DepositRequest request = new DepositRequest();
        request.setAmount(999.0);
        request.setDescription("Invalid test case");

        when(depositService.processDeposit(eq(1L), any(DepositRequest.class)))
                .thenThrow(new RuntimeException("Insufficient account access"));

        ResponseEntity<ApiResponse<Account>> response =
                depositController.processDeposit(VALID_TOKEN, request);

        assertEquals(400, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertTrue(response.getBody().getMessage().contains("Deposit Failed"));
    }
}
