package com.sydney.uni.backend.services;

import com.sydney.uni.backend.entity.User;
import com.sydney.uni.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SettingsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SettingsService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Update name
    public String updateUserName(Long userId, String newName) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return "User not found";
        }

        User user = optionalUser.get();
        user.setName(newName);
        userRepository.save(user);
        return "Name updated successfully";
    }

    // Update email
    public String updateEmail(Long userId, String newEmail) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return "User not found";
        }

        // Validate email format
        if (!newEmail.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")) {
            return "Invalid email format";
        }

        // Check if email already exists
        Optional<User> existingUser = userRepository.findByEmail(newEmail);
        if (existingUser.isPresent() && !existingUser.get().getId().equals(userId)) {
            return "Email already exists";
        }

        User user = optionalUser.get();
        user.setEmail(newEmail);
        userRepository.save(user);
        return "Email updated successfully";
    }

    // Update password (including strong password validation)
    public String updatePassword(Long userId, String currentPassword, String newPassword) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return "User not found";
        }

        User user = optionalUser.get();

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return "Current password is incorrect";
        }

        // Check strong password requirements
        if (newPassword.length() < 8 ||
                !newPassword.matches(".*[A-Z].*") ||
                !newPassword.matches(".*[a-z].*") ||
                !newPassword.matches(".*\\d.*") ||
                !newPassword.matches(".*[@#$%^&+=!].*")) {
            return "Password must include upper, lower, number, and special character";
        }

        // Check if new password is the same as old password
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            return "New password cannot be the same as the old password";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return "Password updated successfully";
    }

    // Delete account
    public String deleteAccount(Long userId) {
        if (!userRepository.existsById(userId)) {
            return "User not found";
        }
        userRepository.deleteById(userId);
        return "Account deleted successfully";
    }
}
