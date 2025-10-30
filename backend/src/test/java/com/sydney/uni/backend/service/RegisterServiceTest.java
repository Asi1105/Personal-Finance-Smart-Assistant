package com.sydney.uni.backend.service;

import com.sydney.uni.backend.dto.AuthResponse;
import com.sydney.uni.backend.dto.RegisterRequest;
import com.sydney.uni.backend.entity.User;
import com.sydney.uni.backend.repository.UserRepository;
import com.sydney.uni.backend.services.RegisterService;
import com.sydney.uni.backend.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RegisterServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private RegisterService registerService;

    private RegisterRequest registerRequest;
    private User savedUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest("Leo Liu", "test@example.com", "123456");

        savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("Leo Liu");
        savedUser.setEmail("test@example.com");
        savedUser.setPassword("encodedPassword");
        savedUser.setCreatedAt(LocalDateTime.now());
    }

    /**
    Registration successful (new user)
     */
    @Test
    void testRegister_Successful() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("123456")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtUtil.generateToken("test@example.com", 1L)).thenReturn("mock-jwt-token");

        AuthResponse response = registerService.register(registerRequest);

        assertNotNull(response);
        assertNotNull(response.getUser());
        assertEquals("mock-jwt-token", response.getToken());
        assertEquals("Leo Liu", response.getUser().getName());
        assertEquals("test@example.com", response.getUser().getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    /**
    Registration failed (email address already exists)
     */
    @Test
    void testRegister_EmailAlreadyExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(savedUser));

        AuthResponse response = registerService.register(registerRequest);

        assertNull(response);
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }
}
