package com.sydney.uni.backend.services;

import com.sydney.uni.backend.dto.BudgetRequest;
import com.sydney.uni.backend.dto.BudgetDto;
import com.sydney.uni.backend.entity.*;
import com.sydney.uni.backend.repository.BudgetRepository;
import com.sydney.uni.backend.repository.TransactionRepository;
import com.sydney.uni.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BudgetService {
    
    private static final String BUDGET_NOT_FOUND_MESSAGE = "Budget not found";

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public BudgetService(BudgetRepository budgetRepository, UserRepository userRepository, TransactionRepository transactionRepository) {
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Budget addBudget(Long userId, BudgetRequest budgetRequest) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if budget already exists for this category and period
        Optional<Budget> existingBudget = budgetRepository.findByUserIdAndCategoryAndPeriod(
            userId, budgetRequest.getCategory(), budgetRequest.getPeriod());
        if (existingBudget.isPresent()) {
            // Update existing budget
            Budget budget = existingBudget.get();
            budget.setAmount(budgetRequest.getAmount());
            return budgetRepository.save(budget);
        }
        
        // Create new budget
        Budget budget = new Budget();
        budget.setUser(user);
        budget.setCategory(budgetRequest.getCategory());
        budget.setPeriod(budgetRequest.getPeriod());
        budget.setAmount(budgetRequest.getAmount());
        
        return budgetRepository.save(budget);
    }

    public List<Budget> getUserBudgets(Long userId) {
        return budgetRepository.findByUserId(userId);
    }

    public List<BudgetDto> getUserBudgetsWithSpending(Long userId) {
        List<Budget> budgets = budgetRepository.findByUserId(userId);
        return budgets.stream()
            .map(budget -> convertToBudgetDto(budget, userId))
            .toList();
    }

    private BudgetDto convertToBudgetDto(Budget budget, Long userId) {
        BudgetDto dto = new BudgetDto();
        dto.setId(budget.getId());
        dto.setCategory(budget.getCategory());
        dto.setPeriod(budget.getPeriod());
        dto.setAmount(budget.getAmount());
        
        // Calculate spent amount for this category
        Double spent = calculateSpentAmount(userId, budget.getCategory());
        dto.setSpent(spent);
        dto.setRemaining(budget.getAmount() - spent);
        dto.setUtilizationPercentage((spent / budget.getAmount()) * 100);
        
        return dto;
    }

    private Double calculateSpentAmount(Long userId, String category) {
        // Get current month's start and end dates
        LocalDate now = LocalDate.now();
        LocalDate monthStart = now.withDayOfMonth(1);
        LocalDate monthEnd = now.withDayOfMonth(now.lengthOfMonth());
        
        // Get current month's expense transactions for this user
        List<Transaction> expenses = transactionRepository.findByAccountUserIdAndDateBetweenAndType(
            userId, monthStart, monthEnd, TransactionType.OUT);
        
        // Filter by category - compare enum values directly
        return expenses.stream()
            .filter(transaction -> {
                if (transaction.getExpenseCategory() == null) return false;
                // Compare enum name with budget category string
                return transaction.getExpenseCategory().name().equals(category);
            })
            .mapToDouble(Transaction::getAmount)
            .sum();
    }


    public Optional<Budget> getUserBudgetByPeriod(Long userId, String period) {
        return budgetRepository.findByUserIdAndPeriod(userId, period);
    }

    @Transactional
    public void deleteBudget(Long userId, Long budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
            .orElseThrow(() -> new RuntimeException(BUDGET_NOT_FOUND_MESSAGE));
        
        // Check if the budget belongs to the user
        if (!budget.getUser().getId().equals(userId)) {
            throw new RuntimeException("Budget does not belong to user");
        }
        
        budgetRepository.delete(budget);
    }

    public BudgetDto getBudgetById(Long budgetId, Long userId) {
        Budget budget = budgetRepository.findById(budgetId)
            .orElseThrow(() -> new RuntimeException(BUDGET_NOT_FOUND_MESSAGE));
        
        // Check if the budget belongs to the user
        if (!budget.getUser().getId().equals(userId)) {
            throw new RuntimeException("Budget does not belong to user");
        }
        
        return convertToBudgetDto(budget, userId);
    }

    @Transactional
    public Budget updateBudget(Long budgetId, Long userId, BudgetRequest budgetRequest) {
        Budget budget = budgetRepository.findById(budgetId)
            .orElseThrow(() -> new RuntimeException(BUDGET_NOT_FOUND_MESSAGE));
        
        // Check if the budget belongs to the user
        if (!budget.getUser().getId().equals(userId)) {
            throw new RuntimeException("Budget does not belong to user");
        }
        
        // Update budget fields
        budget.setCategory(budgetRequest.getCategory());
        budget.setPeriod(budgetRequest.getPeriod());
        budget.setAmount(budgetRequest.getAmount());
        
        return budgetRepository.save(budget);
    }
}
