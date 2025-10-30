package com.sydney.uni.backend.controller;

import com.sydney.uni.backend.dto.ApiResponse;
import com.sydney.uni.backend.dto.SaveMoneyRequest;
import com.sydney.uni.backend.entity.Account;
import com.sydney.uni.backend.services.SaveMoneyService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SaveMoneyControllerTest {

    @Mock
    private SaveMoneyService saveMoneyService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private SaveMoneyController saveMoneyController;

    private static final String VALID_TOKEN = "Bearer valid.jwt.token";
    private static final String VALID_TOKEN_VALUE = "valid.jwt.token";
    private static final String INVALID_TOKEN = "Bearer invalid.token";
    private static final String INVALID_TOKEN_VALUE = "invalid.token";
    private static final Long USER_ID = 1L;

    private SaveMoneyRequest saveMoneyRequest;
    private Account mockAccount;

    @BeforeEach
    void setup() {
        // 准备一个可重用的 Request DTO
        saveMoneyRequest = new SaveMoneyRequest();
        saveMoneyRequest.setAmount(100.0);
        saveMoneyRequest.setDescription("Test save");

        // 准备一个可重用的 Account 实体
        mockAccount = new Account();
        // mockAccount.setId(1);
        // mockAccount.setBalance(1000.0);
        // mockAccount.setSaved(100.0);

        // 模拟通用的 Token 校验
        when(jwtUtil.extractUserId(VALID_TOKEN_VALUE)).thenReturn(USER_ID);
    }

    @Test
    void testSaveMoney_Success() {
        // Arrange
        when(saveMoneyService.saveMoney(eq(USER_ID), any(SaveMoneyRequest.class)))
                .thenReturn(mockAccount);

        // Act
        ResponseEntity<ApiResponse<Account>> response =
                saveMoneyController.saveMoney(VALID_TOKEN, saveMoneyRequest);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(mockAccount, response.getBody().getData());
    }

    @Test
    void testSaveMoney_Unauthorized_NoToken() {
        // Act (Token is null)
        ResponseEntity<ApiResponse<Account>> response =
                saveMoneyController.saveMoney(null, saveMoneyRequest);

        // Assert
        assertEquals(401, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("UNAUTHORIZED", response.getBody().getError().getCode());
    }

    @Test
    void testSaveMoney_Unauthorized_BadFormat() {
        // Act (Token does not start with "Bearer ")
        ResponseEntity<ApiResponse<Account>> response =
                saveMoneyController.saveMoney("Invalid format", saveMoneyRequest);

        // Assert
        assertEquals(401, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("UNAUTHORIZED", response.getBody().getError().getCode());
    }

    @Test
    void testSaveMoney_InvalidToken_JwtException() {
        // Arrange (Mock JwtUtil to throw exception)
        when(jwtUtil.extractUserId(INVALID_TOKEN_VALUE)).thenThrow(new JwtException("Invalid signature"));

        // Act
        ResponseEntity<ApiResponse<Account>> response =
                saveMoneyController.saveMoney(INVALID_TOKEN, saveMoneyRequest);

        // Assert (Controller catches Exception and returns 400)
        assertEquals(400, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("SAVE_MONEY_FAILED", response.getBody().getError().getCode());
        assertTrue(response.getBody().getError().getMessage().contains("Invalid signature"));
    }

    @Test
    void testSaveMoney_ServiceFailure_RuntimeException() {
        // Arrange (Mock Service to throw exception)
        when(saveMoneyService.saveMoney(eq(USER_ID), any(SaveMoneyRequest.class)))
                .thenThrow(new RuntimeException("Insufficient funds"));

        // Act
        ResponseEntity<ApiResponse<Account>> response =
                saveMoneyController.saveMoney(VALID_TOKEN, saveMoneyRequest);

        // Assert (Controller catches Exception and returns 400)
        assertEquals(400, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("SAVE_MONEY_FAILED", response.getBody().getError().getCode());
        assertTrue(response.getBody().getError().getMessage().contains("Insufficient funds"));
    }

    @Test
    void testSaveMoney_InvalidToken_UserIdNull() {
        // Arrange (Override setup)
        when(jwtUtil.extractUserId(VALID_TOKEN_VALUE)).thenReturn(null);

        // Act
        ResponseEntity<ApiResponse<Account>> response =
                saveMoneyController.saveMoney(VALID_TOKEN, saveMoneyRequest);

        // Assert (Controller falls through to the final return 401)
        assertEquals(401, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("INVALID_TOKEN", response.getBody().getError().getCode());
    }
}