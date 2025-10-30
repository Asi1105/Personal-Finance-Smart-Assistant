package com.sydney.uni.backend.controller;

import com.sydney.uni.backend.dto.*;
import com.sydney.uni.backend.services.LoginService;
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
class LoginControllerTest {

    @Mock
    private LoginService loginService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private LoginController loginController;

    private static final String VALID_TOKEN = "Bearer valid.jwt.token";

    private LoginRequest validLoginRequest;

    @BeforeEach
    void setup() {
        validLoginRequest = new LoginRequest("test@example.com", "password123");
    }

    @Test
    void testLoginUser_Success() {
        UserDto user = new UserDto("1", "Test User", "test@example.com", "2025-10-30");
        AuthResponse authResponse = new AuthResponse(user, "valid.token");

        when(loginService.login(validLoginRequest)).thenReturn(authResponse);

        ResponseEntity<ApiResponse<AuthResponse>> response =
                loginController.loginUser(validLoginRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        assertNotNull(response.getBody().getData());
        assertEquals("valid.token", response.getBody().getData().getToken());
        verify(loginService, times(1)).login(validLoginRequest);
    }

    @Test
    void testLoginUser_Failed_IncorrectPassword() {
        AuthResponse authResponse = new AuthResponse(null, null);
        when(loginService.login(validLoginRequest)).thenReturn(authResponse);
        when(loginService.checkUserExists(validLoginRequest.getEmail())).thenReturn(true);

        ResponseEntity<ApiResponse<AuthResponse>> response =
                loginController.loginUser(validLoginRequest);

        assertEquals(401, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Authentication Failed", response.getBody().getMessage());
        assertTrue(response.getBody().getError().getMessage().contains("Incorrect password"));
    }

    @Test
    void testLoginUser_Failed_UserNotFound() {
        AuthResponse authResponse = new AuthResponse(null, null);
        when(loginService.login(validLoginRequest)).thenReturn(authResponse);
        when(loginService.checkUserExists(validLoginRequest.getEmail())).thenReturn(false);

        ResponseEntity<ApiResponse<AuthResponse>> response =
                loginController.loginUser(validLoginRequest);

        assertEquals(401, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertTrue(response.getBody().getError().getMessage().contains("User not found"));
    }

    @Test
    void testLogoutUser_Success() {
        ResponseEntity<ApiResponse<String>> response = loginController.logoutUser();

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Logged out successfully", response.getBody().getData());
    }

    @Test
    void testGetCurrentUser_Success() {
        when(jwtUtil.extractUserId(anyString())).thenReturn(1L);
        UserDto user = new UserDto("1", "John Doe", "john@example.com", "2025-10-30");
        when(loginService.getUserById(1L)).thenReturn(user);

        ResponseEntity<ApiResponse<UserDto>> response = loginController.getCurrentUser(VALID_TOKEN);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        assertNotNull(response.getBody().getData());
        assertEquals("John Doe", response.getBody().getData().getName());
        verify(loginService, times(1)).getUserById(1L);
    }

    @Test
    void testGetCurrentUser_NoToken() {
        ResponseEntity<ApiResponse<UserDto>> response = loginController.getCurrentUser(null);

        assertEquals(401, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Unauthorized", response.getBody().getMessage());
        verify(loginService, never()).getUserById(anyLong());
    }

    @Test
    void testGetCurrentUser_InvalidToken() {
        when(jwtUtil.extractUserId(anyString())).thenThrow(new RuntimeException("Invalid token"));

        ResponseEntity<ApiResponse<UserDto>> response =
                loginController.getCurrentUser("Bearer invalid.token");

        assertEquals(401, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Invalid token", response.getBody().getMessage());
    }

    @Test
    void testGetCurrentUser_UserNotFound() {
        when(jwtUtil.extractUserId(anyString())).thenReturn(1L);
        when(loginService.getUserById(1L)).thenReturn(null);

        ResponseEntity<ApiResponse<UserDto>> response =
                loginController.getCurrentUser(VALID_TOKEN);

        assertEquals(401, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Invalid token", response.getBody().getMessage());
    }
}
