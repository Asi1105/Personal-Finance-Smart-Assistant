package com.sydney.uni.backend.services;

import com.sydney.uni.backend.dto.AuthResponse;
import com.sydney.uni.backend.dto.RegisterRequest;
import com.sydney.uni.backend.dto.UserDto;
import com.sydney.uni.backend.entity.User;
import com.sydney.uni.backend.repository.UserRepository;
import com.sydney.uni.backend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RegisterService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            return null;
        }

        User newUser = new User();
        newUser.setName(registerRequest.getName());
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword())); // Password encryption
        newUser.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(newUser);

        UserDto userDto = new UserDto();
        userDto.setId(String.valueOf(savedUser.getId()));
        userDto.setName(savedUser.getName());
        userDto.setEmail(savedUser.getEmail());
        userDto.setCreatedAt(savedUser.getCreatedAt().toString());

        String token = jwtUtil.generateToken(savedUser.getEmail(), savedUser.getId());

        return new AuthResponse(userDto, token);
    }
}