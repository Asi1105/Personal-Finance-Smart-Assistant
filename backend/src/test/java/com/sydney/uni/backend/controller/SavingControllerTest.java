package com.sydney.uni.backend.controller;

import com.sydney.uni.backend.dto.ApiResponse;
import com.sydney.uni.backend.dto.SaveMoneyRequest;
import com.sydney.uni.backend.dto.SavingLogDto;
import com.sydney.uni.backend.dto.UnsaveMoneyRequest;
import com.sydney.uni.backend.entity.Account;
import com.sydney.uni.backend.entity.SavingAction;
import com.sydney.uni.backend.entity.SavingLog;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SavingControllerTest {

    @Mock
    private SaveMoneyService saveMoneyService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private SavingController savingController;

    private static final String VALID_TOKEN = "Bearer valid.jwt.token";
    private static final String VALID_TOKEN_VALUE = "valid.jwt.token";
    private static final String INVALID_TOKEN = "Bearer invalid.token";
    private static final String INVALID_TOKEN_VALUE = "invalid.token";
    private static final Long USER_ID = 1L;

    private Account mockAccount;

    @BeforeEach
    void setup() {
        when(jwtUtil.extractUserId(VALID_TOKEN_VALUE)).thenReturn(USER_ID);

        mockAccount = new Account();
    }


    @Test
    void testSaveMoney_Success() {
        SaveMoneyRequest request = new SaveMoneyRequest();
        request.setAmount(100.0);
        request.setDescription("Saving for holiday");

        when(saveMoneyService.saveMoney(eq(USER_ID), any(SaveMoneyRequest.class)))
                .thenReturn(mockAccount);

        ResponseEntity<ApiResponse<Account>> response =
                savingController.saveMoney(VALID_TOKEN, request);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(mockAccount, response.getBody().getData());
    }

    @Test
    void testSaveMoney_Unauthorized() {
        SaveMoneyRequest request = new SaveMoneyRequest();

        ResponseEntity<ApiResponse<Account>> response =
                savingController.saveMoney(null, request);

        assertEquals(401, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("UNAUTHORIZED", response.getBody().getError().getCode());
    }

    @Test
    void testSaveMoney_HeaderWithoutBearer_Unauthorized() {
        SaveMoneyRequest request = new SaveMoneyRequest();
        ResponseEntity<ApiResponse<Account>> response = savingController.saveMoney("Token abc", request);
        assertEquals(401, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("UNAUTHORIZED", response.getBody().getError().getCode());
    }

    @Test
    void testSaveMoney_InvalidToken() {
        SaveMoneyRequest request = new SaveMoneyRequest();
        when(jwtUtil.extractUserId(INVALID_TOKEN_VALUE)).thenThrow(new JwtException("Invalid signature"));

        ResponseEntity<ApiResponse<Account>> response =
                savingController.saveMoney(INVALID_TOKEN, request);

        assertEquals(400, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("SAVE_MONEY_FAILED", response.getBody().getError().getCode()); // 修正 Error Code
        assertEquals("Invalid signature", response.getBody().getError().getMessage()); // 验证异常信息
    }

    @Test
    void testSaveMoney_ServiceFailure() {
        SaveMoneyRequest request = new SaveMoneyRequest();
        when(saveMoneyService.saveMoney(eq(USER_ID), any(SaveMoneyRequest.class)))
                .thenThrow(new RuntimeException("Insufficient funds"));

        ResponseEntity<ApiResponse<Account>> response =
                savingController.saveMoney(VALID_TOKEN, request);

        assertEquals(400, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("SAVE_MONEY_FAILED", response.getBody().getError().getCode());
        assertEquals("Insufficient funds", response.getBody().getError().getMessage());
    }

    @Test
    void testSaveMoney_UserIdNull_Unauthorized() {
        SaveMoneyRequest request = new SaveMoneyRequest();
        when(jwtUtil.extractUserId(VALID_TOKEN_VALUE)).thenReturn(null);
        ResponseEntity<ApiResponse<Account>> response = savingController.saveMoney(VALID_TOKEN, request);
        assertEquals(401, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("INVALID_TOKEN", response.getBody().getError().getCode());
    }


    @Test
    void testUnsaveMoney_Success() {
        UnsaveMoneyRequest request = new UnsaveMoneyRequest();
        request.setAmount(50.0);
        request.setDescription("Need cash");

        when(saveMoneyService.unsaveMoney(eq(USER_ID), any(UnsaveMoneyRequest.class)))
                .thenReturn(mockAccount);

        ResponseEntity<ApiResponse<Account>> response =
                savingController.unsaveMoney(VALID_TOKEN, request);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        assertEquals(mockAccount, response.getBody().getData());
    }

    @Test
    void testUnsaveMoney_Unauthorized() {
        UnsaveMoneyRequest request = new UnsaveMoneyRequest();

        ResponseEntity<ApiResponse<Account>> response =
                savingController.unsaveMoney("Not Bearer", request);

        assertEquals(401, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("UNAUTHORIZED", response.getBody().getError().getCode());
    }

    @Test
    void testUnsaveMoney_UserIdNull_Unauthorized() {
        UnsaveMoneyRequest request = new UnsaveMoneyRequest();
        when(jwtUtil.extractUserId(VALID_TOKEN_VALUE)).thenReturn(null);
        ResponseEntity<ApiResponse<Account>> response = savingController.unsaveMoney(VALID_TOKEN, request);
        assertEquals(401, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("INVALID_TOKEN", response.getBody().getError().getCode());
    }

    @Test
    void testUnsaveMoney_ServiceFailure() {
        UnsaveMoneyRequest request = new UnsaveMoneyRequest();
        when(saveMoneyService.unsaveMoney(eq(USER_ID), any(UnsaveMoneyRequest.class)))
                .thenThrow(new RuntimeException("Amount exceeds saved balance"));

        ResponseEntity<ApiResponse<Account>> response =
                savingController.unsaveMoney(VALID_TOKEN, request);

        assertEquals(400, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("UNSAVE_MONEY_FAILED", response.getBody().getError().getCode());
        assertEquals("Amount exceeds saved balance", response.getBody().getError().getMessage());
    }


    @Test
    void testGetSavingLogs_Success() {
        SavingLog log1 = new SavingLog();
        log1.setId(1L);
        log1.setAction(SavingAction.SAVE);
        log1.setAmount(100.0);
        log1.setDescription("Saved");
        log1.setTimestamp(LocalDateTime.now());

        SavingLog log2 = new SavingLog();
        log2.setId(2L);
        log2.setAction(SavingAction.UNSAVE);
        log2.setAmount(50.0);
        log2.setDescription("Unsaved");
        log2.setTimestamp(LocalDateTime.now().minusDays(1));

        when(saveMoneyService.getSavingLogs(USER_ID)).thenReturn(List.of(log1, log2));

        ResponseEntity<ApiResponse<List<SavingLogDto>>> response =
                savingController.getSavingLogs(VALID_TOKEN);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        assertEquals(2, response.getBody().getData().size());

        SavingLogDto dto1 = response.getBody().getData().get(0);
        assertEquals(1L, dto1.getId());
        assertEquals(SavingAction.SAVE, dto1.getAction());
        assertEquals("Money Saved", dto1.getActionDisplayName()); // 验证转换
        assertEquals("💰", dto1.getIcon()); // 验证转换

        SavingLogDto dto2 = response.getBody().getData().get(1);
        assertEquals(2L, dto2.getId());
        assertEquals(SavingAction.UNSAVE, dto2.getAction());
        assertEquals("Money Unmarked", dto2.getActionDisplayName()); // 验证转换
        assertEquals("💸", dto2.getIcon()); // 验证转换
    }

    @Test
    void testGetSavingLogs_Unauthorized() {
        ResponseEntity<ApiResponse<List<SavingLogDto>>> response =
                savingController.getSavingLogs(null);

        assertEquals(401, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("UNAUTHORIZED", response.getBody().getError().getCode());
    }

    @Test
    void testGetSavingLogs_HeaderWithoutBearer_Unauthorized() {
        ResponseEntity<ApiResponse<List<SavingLogDto>>> response = savingController.getSavingLogs("Token x");
        assertEquals(401, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("UNAUTHORIZED", response.getBody().getError().getCode());
    }

    @Test
    void testGetSavingLogs_UserIdNull_Unauthorized() {
        when(jwtUtil.extractUserId(VALID_TOKEN_VALUE)).thenReturn(null);
        ResponseEntity<ApiResponse<List<SavingLogDto>>> response = savingController.getSavingLogs(VALID_TOKEN);
        assertEquals(401, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("INVALID_TOKEN", response.getBody().getError().getCode());
    }

    @Test
    void testGetSavingLogs_InvalidToken() {
        when(jwtUtil.extractUserId(INVALID_TOKEN_VALUE)).thenThrow(new JwtException("Invalid signature"));

        ResponseEntity<ApiResponse<List<SavingLogDto>>> response =
                savingController.getSavingLogs(INVALID_TOKEN);

        assertEquals(400, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("GET_SAVING_LOGS_FAILED", response.getBody().getError().getCode());
        assertTrue(response.getBody().getError().getMessage().contains("Invalid signature"));
    }
}