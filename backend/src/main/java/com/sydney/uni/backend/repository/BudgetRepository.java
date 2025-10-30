package com.sydney.uni.backend.repository;

import com.sydney.uni.backend.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    Optional<Budget> findByUserIdAndPeriod(Long userId, String period);
    Optional<Budget> findByUserIdAndCategoryAndPeriod(Long userId, String category, String period);
    List<Budget> findByUserId(Long userId);
}
