package com.sydney.uni.backend.repository;

import com.sydney.uni.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);       // Find user by email for login
}