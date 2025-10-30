package com.sydney.uni.backend.controller;

import com.sydney.uni.backend.dto.ApiResponse;
import com.sydney.uni.backend.services.SettingsService;
import com.sydney.uni.backend.utils.JwtUtil;
import io.jsonwebtoken.JwtException; // 确保导入了 JwtUtil 可能抛出的异常
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SettingsControllerTest {

    @Mock
    private SettingsService settingsService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private SettingsController settingsController;

    private static final String VALID_TOKEN = "Bearer valid.jwt.token";
    private static final String INVALID_TOKEN_VALUE = "invalid.token";
    private static final Long USER_ID = 1L;

    @BeforeEach
    void setup() {
        when(jwtUtil.extractUserId("valid.jwt.token")).thenReturn(USER_ID);
    }

    // --- Update Name Tests ---

    @Test
    void testUpdateUserName_Success() {
        String newName = "New Name";
        String successMessage = "Name updated successfully";
        when(settingsService.updateUserName(USER_ID, newName)).thenReturn(successMessage);

        ResponseEntity<ApiResponse<String>> response =
                settingsController.updateUserName(VALID_TOKEN, newName);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(successMessage, response.getBody().getData());
    }

    @Test
    void testUpdateUserName_Unauthorized() {
        ResponseEntity<ApiResponse<String>> response =
                settingsController.updateUserName(null, "New Name");

        assertEquals(401, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("UNAUTHORIZED", response.getBody().getError().getCode());
    }

    @Test
    void testUpdateUserName_InvalidToken() {
        when(jwtUtil.extractUserId(INVALID_TOKEN_VALUE)).thenThrow(new JwtException("Invalid signature"));

        ResponseEntity<ApiResponse<String>> response =
                settingsController.updateUserName("Bearer " + INVALID_TOKEN_VALUE, "New Name");

        assertEquals(400, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("UPDATE_FAILED", response.getBody().getError().getCode());
        assertEquals("Invalid signature", response.getBody().getError().getMessage());
    }

    // --- Update Email Tests ---

    @Test
    void testUpdateEmail_Success() {
        String newEmail = "new@example.com";
        String successMessage = "Email updated successfully";
        when(settingsService.updateEmail(USER_ID, newEmail)).thenReturn(successMessage);

        ResponseEntity<ApiResponse<String>> response =
                settingsController.updateEmail(VALID_TOKEN, newEmail);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(successMessage, response.getBody().getData());
    }

    @Test
    void testUpdateEmail_Unauthorized() {
        ResponseEntity<ApiResponse<String>> response =
                settingsController.updateEmail("invalid token format", "new@example.com");

        assertEquals(401, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("UNAUTHORIZED", response.getBody().getError().getCode());
    }

    @Test
    void testUpdateEmail_ServiceFailure() {
        String newEmail = "new@example.com";
        when(settingsService.updateEmail(USER_ID, newEmail)).thenThrow(new RuntimeException("Email already exists"));

        ResponseEntity<ApiResponse<String>> response =
                settingsController.updateEmail(VALID_TOKEN, newEmail);

        assertEquals(400, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("UPDATE_FAILED", response.getBody().getError().getCode());
        assertEquals("Email already exists", response.getBody().getError().getMessage());
    }

    // --- Update Password Tests ---

    @Test
    void testUpdatePassword_Success() {
        // Arrange
        String successMessage = "Password updated successfully";
        when(settingsService.updatePassword(USER_ID, "currentPass", "newPass")).thenReturn(successMessage);

        // Act
        ResponseEntity<ApiResponse<String>> response =
                settingsController.updatePassword(VALID_TOKEN, "currentPass", "newPass");

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(successMessage, response.getBody().getData());
    }

    @Test
    void testUpdatePassword_ValidationFailed() {
        String validationError = "Invalid current password";
        when(settingsService.updatePassword(USER_ID, "wrongPass", "newPass")).thenReturn(validationError);

        ResponseEntity<ApiResponse<String>> response =
                settingsController.updatePassword(VALID_TOKEN, "wrongPass", "newPass");

        assertEquals(400, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("VALIDATION_FAILED", response.getBody().getError().getCode());
        assertEquals(validationError, response.getBody().getError().getMessage());
    }

    @Test
    void testUpdatePassword_Unauthorized() {
        ResponseEntity<ApiResponse<String>> response =
                settingsController.updatePassword(null, "currentPass", "newPass");

        assertEquals(401, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("UNAUTHORIZED", response.getBody().getError().getCode());
    }

    // --- Delete Account Tests ---

    @Test
    void testDeleteAccount_Success() {
        String successMessage = "Account deleted successfully";
        when(settingsService.deleteAccount(USER_ID)).thenReturn(successMessage);

        ResponseEntity<ApiResponse<String>> response =
                settingsController.deleteAccount(VALID_TOKEN);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(successMessage, response.getBody().getData());
    }

    @Test
    void testDeleteAccount_Unauthorized() {
        ResponseEntity<ApiResponse<String>> response =
                settingsController.deleteAccount(null);

        assertEquals(401, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("UNAUTHORIZED", response.getBody().getError().getCode());
    }

    @Test
    void testDeleteAccount_InvalidToken() {
        when(jwtUtil.extractUserId(INVALID_TOKEN_VALUE)).thenThrow(new JwtException("Invalid signature"));

        ResponseEntity<ApiResponse<String>> response =
                settingsController.deleteAccount("Bearer " + INVALID_TOKEN_VALUE);

        assertEquals(400, response.getStatusCodeValue()); // Returns 400 from catch block
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("DELETE_FAILED", response.getBody().getError().getCode());
    }
}