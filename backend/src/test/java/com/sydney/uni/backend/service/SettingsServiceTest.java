package com.sydney.uni.backend.service;

import com.sydney.uni.backend.entity.User;
import com.sydney.uni.backend.repository.UserRepository;
import com.sydney.uni.backend.services.SettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class SettingsServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private SettingsService settingsService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("Alice", "alice@gmail.com", "encodedOldPassword");
        user.setId(1L);
    }

    // 1️⃣ updateUserName ----------------------------------------------------

    @Test
    void updateUserName_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        String result = settingsService.updateUserName(1L, "NewName");
        assertEquals("User not found", result);
    }

    @Test
    void updateUserName_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        String result = settingsService.updateUserName(1L, "NewName");
        assertEquals("Name updated successfully", result);
        verify(userRepository).save(user);
        assertEquals("NewName", user.getName());
    }

    // 2️⃣ updateEmail -------------------------------------------------------

    @Test
    void updateEmail_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        String result = settingsService.updateEmail(1L, "new@gmail.com");
        assertEquals("User not found", result);
    }

    @Test
    void updateEmail_InvalidFormat() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        String result = settingsService.updateEmail(1L, "invalid-email");
        assertEquals("Invalid email format", result);
    }

    @Test
    void updateEmail_AlreadyExists() {
        User another = new User("Bob",  "bob@gmail.com", "pwd");
        another.setId(2L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("bob@gmail.com")).thenReturn(Optional.of(another));

        String result = settingsService.updateEmail(1L, "bob@gmail.com");
        assertEquals("Email already exists", result);
    }

    @Test
    void updateEmail_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("new@gmail.com")).thenReturn(Optional.empty());

        String result = settingsService.updateEmail(1L, "new@gmail.com");
        assertEquals("Email updated successfully", result);
        verify(userRepository).save(user);
        assertEquals("new@gmail.com", user.getEmail());
    }

    // 3️⃣ updatePassword ----------------------------------------------------

    @Test
    void updatePassword_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        String result = settingsService.updatePassword(1L, "oldPwd", "NewPwd@123");
        assertEquals("User not found", result);
    }

    @Test
    void updatePassword_CurrentIncorrect() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encodedOldPassword")).thenReturn(false);

        String result = settingsService.updatePassword(1L, "wrong", "NewPwd@123");
        assertEquals("Current password is incorrect", result);
    }

    @Test
    void updatePassword_WeakPassword() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPwd", "encodedOldPassword")).thenReturn(true);

        String result = settingsService.updatePassword(1L, "oldPwd", "weak");
        assertEquals("Password must include upper, lower, number, and special character", result);
    }

    @Test
    void updatePassword_SameAsOldPassword() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPwd", "encodedOldPassword")).thenReturn(true);
        when(passwordEncoder.matches("OldPwd@123", "encodedOldPassword")).thenReturn(true);

        String result = settingsService.updatePassword(1L, "oldPwd", "OldPwd@123");
        assertEquals("New password cannot be the same as the old password", result);
    }

    @Test
    void updatePassword_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPwd", "encodedOldPassword")).thenReturn(true);
        when(passwordEncoder.matches("NewPwd@123", "encodedOldPassword")).thenReturn(false);
        when(passwordEncoder.encode("NewPwd@123")).thenReturn("encodedNew");

        String result = settingsService.updatePassword(1L, "oldPwd", "NewPwd@123");
        assertEquals("Password updated successfully", result);
        verify(userRepository).save(user);
        assertEquals("encodedNew", user.getPassword());
    }

    // 4️⃣ deleteAccount ----------------------------------------------------

    @Test
    void deleteAccount_NotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        String result = settingsService.deleteAccount(1L);
        assertEquals("User not found", result);
    }

    @Test
    void deleteAccount_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);

        String result = settingsService.deleteAccount(1L);
        assertEquals("Account deleted successfully", result);
        verify(userRepository).deleteById(1L);
    }
}
