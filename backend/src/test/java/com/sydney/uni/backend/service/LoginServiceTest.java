package com.sydney.uni.backend.service;

import com.sydney.uni.backend.dto.AuthResponse;
import com.sydney.uni.backend.dto.LoginRequest;
import com.sydney.uni.backend.dto.UserDto;
import com.sydney.uni.backend.entity.User;
import com.sydney.uni.backend.repository.UserRepository;
import com.sydney.uni.backend.services.LoginService;
import com.sydney.uni.backend.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private LoginService loginService;

    private User user;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Leo Liu");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setCreatedAt(LocalDateTime.now());

        loginRequest = new LoginRequest("test@example.com", "rawPassword");
    }

    @Test
    void testLogin_UserNotFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        AuthResponse response = loginService.login(loginRequest);

        assertNull(response.getUser());
        assertNull(response.getToken());
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testLogin_Successful() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("test@example.com", 1L)).thenReturn("mock-jwt-token");

        AuthResponse response = loginService.login(loginRequest);

        assertNotNull(response.getUser());
        assertEquals("mock-jwt-token", response.getToken());
        assertEquals("Leo Liu", response.getUser().getName());
        verify(jwtUtil, times(1)).generateToken(anyString(), anyLong());
    }

    @Test
    void testLogin_WrongPassword() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(false);

        AuthResponse response = loginService.login(loginRequest);

        assertNull(response.getUser());
        assertNull(response.getToken());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
    }

    @Test
    void testGetUserById_UserFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto dto = loginService.getUserById(1L);

        assertNotNull(dto);
        assertEquals("Leo Liu", dto.getName());
        assertEquals("test@example.com", dto.getEmail());
    }

    @Test
    void testGetUserById_UserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        UserDto dto = loginService.getUserById(99L);

        assertNull(dto);
        verify(userRepository, times(1)).findById(99L);
    }

    @Test
    void testCheckUserExists_UserExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        boolean exists = loginService.checkUserExists("test@example.com");

        assertTrue(exists);
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testCheckUserExists_UserNotExists() {
        when(userRepository.findByEmail("none@example.com")).thenReturn(Optional.empty());

        boolean exists = loginService.checkUserExists("none@example.com");

        assertFalse(exists);
        verify(userRepository, times(1)).findByEmail("none@example.com");
    }
}