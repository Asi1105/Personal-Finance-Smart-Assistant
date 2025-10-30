package com.sydney.uni.backend.controller;

import com.sydney.uni.backend.dto.ApiResponse;
import com.sydney.uni.backend.dto.AuthResponse;
import com.sydney.uni.backend.dto.ErrorResponse;
import com.sydney.uni.backend.dto.LoginRequest;
import com.sydney.uni.backend.dto.UserDto;
import com.sydney.uni.backend.services.LoginService;
import com.sydney.uni.backend.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class LoginController {

    private final LoginService loginService;
    private final JwtUtil jwtUtil;

    public LoginController(LoginService loginService, JwtUtil jwtUtil) {
        this.loginService = loginService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> loginUser(@RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = loginService.login(loginRequest);

        if (authResponse.getToken() != null && authResponse.getUser() != null) {
            return ResponseEntity.ok(ApiResponse.ok(authResponse));
        } else {
            // Check if user exists to provide more specific error message
            boolean userExists = loginService.checkUserExists(loginRequest.getEmail());
            String errorMessage = userExists ? 
                "Authentication failed: Incorrect password" : 
                "Authentication failed: User not found";
            
            ErrorResponse error = new ErrorResponse(errorMessage, "LOGIN_FAILED", null);
            return new ResponseEntity<>(ApiResponse.fail("Authentication Failed", error), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logoutUser() {
        // Simple logout implementation, in real projects should add token to blacklist
        return ResponseEntity.ok(ApiResponse.ok("Logged out successfully"));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            ErrorResponse error = new ErrorResponse("Authorization token required", "UNAUTHORIZED", null);
            return new ResponseEntity<>(ApiResponse.fail("Unauthorized", error), HttpStatus.UNAUTHORIZED);
        }

        try {
            String tokenValue = token.substring(7); // Remove "Bearer " prefix
            Long userId = jwtUtil.extractUserId(tokenValue);
            
            if (userId != null) {
                UserDto userDto = loginService.getUserById(userId);
                
                if (userDto != null) {
                    return ResponseEntity.ok(ApiResponse.ok(userDto));
                }
            }
        } catch (Exception e) {
            // Token parsing failed
        }

        ErrorResponse error = new ErrorResponse("Invalid token", "INVALID_TOKEN", null);
        return new ResponseEntity<>(ApiResponse.fail("Invalid token", error), HttpStatus.UNAUTHORIZED);
    }
}