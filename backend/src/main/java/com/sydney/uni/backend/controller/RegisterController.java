package com.sydney.uni.backend.controller;

import com.sydney.uni.backend.dto.ApiResponse;
import com.sydney.uni.backend.dto.AuthResponse;
import com.sydney.uni.backend.dto.ErrorResponse;
import com.sydney.uni.backend.dto.RegisterRequest;
import com.sydney.uni.backend.services.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class RegisterController {

    @Autowired
    private RegisterService registerService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> registerUser(@RequestBody RegisterRequest registerRequest) {
        AuthResponse authResponse = registerService.register(registerRequest);

        if (authResponse != null) {
            return ResponseEntity.ok(ApiResponse.ok(authResponse));
        } else {
            ErrorResponse error = new ErrorResponse("Registration failed: Email already in use.", "EMAIL_IN_USE", null);
            return new ResponseEntity<>(ApiResponse.fail("Registration Failed", error), HttpStatus.CONFLICT);
        }
    }
}