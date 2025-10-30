package com.sydney.uni.backend.controller;

import com.sydney.uni.backend.dto.ApiResponse;
import com.sydney.uni.backend.dto.ErrorResponse;
import com.sydney.uni.backend.services.SettingsService;
import com.sydney.uni.backend.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    private final SettingsService settingsService;
    private final JwtUtil jwtUtil;

    public SettingsController(SettingsService settingsService, JwtUtil jwtUtil) {
        this.settingsService = settingsService;
        this.jwtUtil = jwtUtil;
    }

    // Update name (automatically identify current user)
    @PutMapping("/update-name")
    public ResponseEntity<ApiResponse<String>> updateUserName(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam String newName) {

        if (token == null || !token.startsWith("Bearer ")) {
            return new ResponseEntity<>(ApiResponse.fail("Unauthorized",
                    new ErrorResponse("Missing or invalid token", "UNAUTHORIZED", null)),
                    HttpStatus.UNAUTHORIZED);
        }

        try {
            String tokenValue = token.substring(7);
            Long userId = jwtUtil.extractUserId(tokenValue);
            String result = settingsService.updateUserName(userId, newName);
            return ResponseEntity.ok(ApiResponse.ok(result));
        } catch (Exception e) {
            return new ResponseEntity<>(ApiResponse.fail("Update failed",
                    new ErrorResponse(e.getMessage(), "UPDATE_FAILED", null)),
                    HttpStatus.BAD_REQUEST);
        }
    }

    // Update email
    @PutMapping("/update-email")
    public ResponseEntity<ApiResponse<String>> updateEmail(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam String newEmail) {

        if (token == null || !token.startsWith("Bearer ")) {
            return new ResponseEntity<>(ApiResponse.fail("Unauthorized",
                    new ErrorResponse("Missing or invalid token", "UNAUTHORIZED", null)),
                    HttpStatus.UNAUTHORIZED);
        }

        try {
            String tokenValue = token.substring(7);
            Long userId = jwtUtil.extractUserId(tokenValue);
            String result = settingsService.updateEmail(userId, newEmail);
            return ResponseEntity.ok(ApiResponse.ok(result));
        } catch (Exception e) {
            return new ResponseEntity<>(ApiResponse.fail("Update failed",
                    new ErrorResponse(e.getMessage(), "UPDATE_FAILED", null)),
                    HttpStatus.BAD_REQUEST);
        }
    }

    // Update password
    @PutMapping("/update-password")
    public ResponseEntity<ApiResponse<String>> updatePassword(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam String currentPassword,
            @RequestParam String newPassword) {

        if (token == null || !token.startsWith("Bearer ")) {
            return new ResponseEntity<>(
                    ApiResponse.fail("Unauthorized",
                            new ErrorResponse("Missing or invalid token", "UNAUTHORIZED", null)),
                    HttpStatus.UNAUTHORIZED
            );
        }

        try {
            String tokenValue = token.substring(7);
            Long userId = jwtUtil.extractUserId(tokenValue);

            String result = settingsService.updatePassword(userId, currentPassword, newPassword);

            // ✅ 只要不是“真正成功”的文案，一律按失败处理
            if (!"Password updated successfully".equals(result)) {
                return new ResponseEntity<>(
                        ApiResponse.fail(result,
                                new ErrorResponse(result, "VALIDATION_FAILED", null)),
                        HttpStatus.BAD_REQUEST
                );
            }

            // ✅ 成功
            return ResponseEntity.ok(ApiResponse.ok(result));
        } catch (Exception e) {
            return new ResponseEntity<>(
                    ApiResponse.fail("Update failed",
                            new ErrorResponse(e.getMessage(), "UPDATE_FAILED", null)),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    // Delete account
    @DeleteMapping("/delete-account")
    public ResponseEntity<ApiResponse<String>> deleteAccount(
            @RequestHeader(value = "Authorization", required = false) String token) {

        if (token == null || !token.startsWith("Bearer ")) {
            return new ResponseEntity<>(ApiResponse.fail("Unauthorized",
                    new ErrorResponse("Missing or invalid token", "UNAUTHORIZED", null)),
                    HttpStatus.UNAUTHORIZED);
        }

        try {
            String tokenValue = token.substring(7);
            Long userId = jwtUtil.extractUserId(tokenValue);
            String result = settingsService.deleteAccount(userId);
            return ResponseEntity.ok(ApiResponse.ok(result));
        } catch (Exception e) {
            return new ResponseEntity<>(ApiResponse.fail("Delete failed",
                    new ErrorResponse(e.getMessage(), "DELETE_FAILED", null)),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
