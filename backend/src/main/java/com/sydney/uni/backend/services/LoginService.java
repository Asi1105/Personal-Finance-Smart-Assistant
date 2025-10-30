package com.sydney.uni.backend.services;

import com.sydney.uni.backend.dto.AuthResponse;
import com.sydney.uni.backend.dto.LoginRequest;
import com.sydney.uni.backend.dto.UserDto;
import com.sydney.uni.backend.entity.User;
import com.sydney.uni.backend.repository.UserRepository;
import com.sydney.uni.backend.utils.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse login(LoginRequest loginRequest) {
        Optional<User> optionalUser = userRepository.findByEmail(loginRequest.getEmail());

        if (optionalUser.isEmpty()) {
            // User not found - return null to indicate failure
            return new AuthResponse(null, null);
        }

        User user = optionalUser.get();

        if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) { // Password verification
            UserDto userDto = new UserDto();
            userDto.setId(String.valueOf(user.getId()));
            userDto.setName(user.getName());
            userDto.setEmail(user.getEmail());
            userDto.setCreatedAt(user.getCreatedAt().toString());

            String token = jwtUtil.generateToken(user.getEmail(), user.getId());

            return new AuthResponse(userDto, token);
        } else {
            // Wrong password - return null to indicate failure
            return new AuthResponse(null, null);
        }
    }

    public UserDto getUserById(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        
        if (optionalUser.isEmpty()) {
            return null;
        }

        User user = optionalUser.get();
        UserDto userDto = new UserDto();
        userDto.setId(String.valueOf(user.getId()));
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setCreatedAt(user.getCreatedAt().toString());

        return userDto;
    }

    public boolean checkUserExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}