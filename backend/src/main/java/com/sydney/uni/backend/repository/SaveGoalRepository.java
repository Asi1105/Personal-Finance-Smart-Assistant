package com.sydney.uni.backend.repository;

import com.sydney.uni.backend.entity.SaveGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SaveGoalRepository extends JpaRepository<SaveGoal, Long> {
    Optional<SaveGoal> findByUserId(Long userId);
}
