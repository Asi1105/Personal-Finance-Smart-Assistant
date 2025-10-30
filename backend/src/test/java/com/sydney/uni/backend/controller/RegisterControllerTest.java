package com.sydney.uni.backend.controller;

import com.sydney.uni.backend.dto.ApiResponse;
import com.sydney.uni.backend.dto.AuthResponse;
import com.sydney.uni.backend.dto.RegisterRequest;
import com.sydney.uni.backend.dto.UserDto;
import com.sydney.uni.backend.services.RegisterService;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RegisterControllerTest {

    @Mock
    private RegisterService registerService;

    @InjectMocks
    private RegisterController registerController;

    private RegisterRequest registerRequest;
    private AuthResponse mockAuthResponse;
    private UserDto mockUserDto;

    @BeforeEach
    void setup() {
        registerRequest = new RegisterRequest();
        registerRequest.setName("Test User");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");

        mockUserDto = new UserDto("1", "Test User", "test@example.com", "2025-10-30T10:00:00");

        mockAuthResponse = new AuthResponse(mockUserDto, "mock.jwt.token");
    }

    @Test
    void testRegisterUser_Success() {
        when(registerService.register(any(RegisterRequest.class))).thenReturn(mockAuthResponse);

        ResponseEntity<ApiResponse<AuthResponse>> response =
                registerController.registerUser(registerRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertNull(response.getBody().getError());
        assertNotNull(response.getBody().getData());
        assertEquals("mock.jwt.token", response.getBody().getData().getToken());
        assertEquals("Test User", response.getBody().getData().getUser().getName());
    }

    @Test
    void testRegisterUser_Failure_EmailInUse() {
        when(registerService.register(any(RegisterRequest.class))).thenReturn(null);

        ResponseEntity<ApiResponse<AuthResponse>> response =
                registerController.registerUser(registerRequest);

        assertEquals(409, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertNull(response.getBody().getData());
        assertNotNull(response.getBody().getError());
        assertEquals("EMAIL_IN_USE", response.getBody().getError().getCode());
        assertEquals("Registration failed: Email already in use.", response.getBody().getError().getMessage());
    }
}