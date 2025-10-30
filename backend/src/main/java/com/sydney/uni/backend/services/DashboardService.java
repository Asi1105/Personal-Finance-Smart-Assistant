package com.sydney.uni.backend.services;

import com.sydney.uni.backend.dto.DashboardStatsDto;
import com.sydney.uni.backend.dto.TransactionDto;
import com.sydney.uni.backend.entity.*;
import com.sydney.uni.backend.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
public class DashboardService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final SaveGoalRepository saveGoalRepository;

    public DashboardService(AccountRepository accountRepository,
                           TransactionRepository transactionRepository,
                           BudgetRepository budgetRepository,
                           SaveGoalRepository saveGoalRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
        this.saveGoalRepository = saveGoalRepository;
    }

    public DashboardStatsDto getDashboardStats(Long userId) {
        DashboardStatsDto stats = new DashboardStatsDto();

        // Get total balance and saved amount from user's accounts
        List<Account> accounts = accountRepository.findByUserId(userId);
        Double totalBalance = accounts.stream()
                .mapToDouble(account -> account.getBalance() != null ? account.getBalance() : 0.0)
                .sum();
        stats.setTotalBalance(totalBalance);
        
        Double saved = accounts.stream()
                .mapToDouble(account -> account.getSaved() != null ? account.getSaved() : 0.0)
                .sum();
        stats.setSaved(saved);

        // Get current month's spending
        YearMonth currentMonth = YearMonth.now();
        LocalDate startOfMonth = currentMonth.atDay(1);
        LocalDate endOfMonth = currentMonth.atEndOfMonth();

        List<Transaction> monthlyTransactions = transactionRepository.findByAccountUserIdAndDateBetweenAndType(
                userId, startOfMonth, endOfMonth, TransactionType.OUT);
        Double monthlySpending = monthlyTransactions.stream()
                .mapToDouble(transaction -> transaction.getAmount() != null ? transaction.getAmount() : 0.0)
                .sum();
        stats.setMonthlySpending(monthlySpending);

        // Get last month's spending for comparison
        YearMonth lastMonth = currentMonth.minusMonths(1);
        LocalDate startOfLastMonth = lastMonth.atDay(1);
        LocalDate endOfLastMonth = lastMonth.atEndOfMonth();

        List<Transaction> lastMonthTransactions = transactionRepository.findByAccountUserIdAndDateBetweenAndType(
                userId, startOfLastMonth, endOfLastMonth, TransactionType.OUT);
        Double lastMonthSpending = lastMonthTransactions.stream()
                .mapToDouble(transaction -> transaction.getAmount() != null ? transaction.getAmount() : 0.0)
                .sum();

        // Set last month spending for frontend display
        stats.setLastMonthSpending(lastMonthSpending);
        
        // Calculate spending change percentage
        if (lastMonthSpending > 0) {
            Double spendingChange = ((monthlySpending - lastMonthSpending) / lastMonthSpending) * 100;
            stats.setMonthlySpendingChange(spendingChange);
        } else if (monthlySpending > 0) {
            // If last month was 0 and this month has spending, it's a 100% increase
            stats.setMonthlySpendingChange(100.0);
        } else {
            // Both months are 0, no change
            stats.setMonthlySpendingChange(0.0);
        }

        // Get budget information - sum all monthly budgets
        List<Budget> monthlyBudgets = budgetRepository.findByUserId(userId).stream()
                .filter(budget -> "monthly".equals(budget.getPeriod()))
                .toList();
        
        if (!monthlyBudgets.isEmpty()) {
            Double totalBudgetAmount = monthlyBudgets.stream()
                    .mapToDouble(Budget::getAmount)
                    .sum();
            Double budgetUsed = monthlySpending;
            Double budgetLeft = totalBudgetAmount - budgetUsed;
            Double budgetUsedPercentage = totalBudgetAmount > 0 ? (budgetUsed / totalBudgetAmount) * 100 : 0.0;

            stats.setBudgetLeft(Math.max(0, budgetLeft));
            stats.setBudgetUsedPercentage(budgetUsedPercentage);
        } else {
            stats.setBudgetLeft(0.0);
            stats.setBudgetUsedPercentage(null); // Set to null when no budget is set
        }

        // Get savings goal
        Optional<SaveGoal> saveGoal = saveGoalRepository.findByUserId(userId);
        if (saveGoal.isPresent()) {
            Double targetAmount = saveGoal.get().getTargetAmount();
            Double savedAmount = accounts.stream()
                    .mapToDouble(account -> account.getSaved() != null ? account.getSaved() : 0.0)
                    .sum();
            
            stats.setSavingsGoal(targetAmount);
            stats.setHasSavingsGoal(true);
            
            if (targetAmount > 0) {
                Double progress = (savedAmount / targetAmount) * 100;
                stats.setSavingsProgress(progress);
            } else {
                stats.setSavingsProgress(0.0);
            }
        } else {
            stats.setSavingsGoal(0.0);
            stats.setSavingsProgress(0.0);
            stats.setHasSavingsGoal(false);
        }

        return stats;
    }

    public List<TransactionDto> getRecentTransactions(Long userId, int limit) {
        List<Transaction> transactions = transactionRepository.findTop10ByAccountUserIdOrderByDateDesc(userId);
        
        return transactions.stream()
                .map(this::convertToTransactionDto)
                .limit(limit)
                .toList();
    }

    public TransactionDto convertToTransactionDto(Transaction transaction) {
        TransactionDto dto = new TransactionDto();
        dto.setId(transaction.getId());
        dto.setType(transaction.getType());
        dto.setDate(transaction.getDate());
        dto.setExpenseCategory(transaction.getExpenseCategory());
        dto.setDetail(transaction.getDetail());
        dto.setAmount(transaction.getAmount());
        dto.setNote(transaction.getNote());
        
        // Set display name and icon based on category
        if (transaction.getExpenseCategory() != null) {
            dto.setCategoryDisplayName(getCategoryDisplayName(transaction.getExpenseCategory()));
            dto.setIcon(getCategoryIcon(transaction.getExpenseCategory()));
        } else if (transaction.getType() == TransactionType.IN) {
            // For IN transactions (deposits), show "Deposit" instead of "Other"
            dto.setCategoryDisplayName("Deposit");
            dto.setIcon("💰");
        } else {
            dto.setCategoryDisplayName("Other");
            dto.setIcon("💰");
        }
        
        return dto;
    }

    private String getCategoryDisplayName(ExpenseCategory category) {
        switch (category) {
            case FOOD_DINING:
                return "Food & Dining";
            case TRANSPORTATION:
                return "Transportation";
            case ENTERTAINMENT:
                return "Entertainment";
            case SHOPPING:
                return "Shopping";
            case BILLS_UTILITIES:
                return "Bills & Utilities";
            case HEALTHCARE:
                return "Healthcare";
            case TRAVEL:
                return "Travel";
            case EDUCATION:
                return "Education";
            default:
                return "Other";
        }
    }

    private String getCategoryIcon(ExpenseCategory category) {
        switch (category) {
            case FOOD_DINING:
                return "🍕";
            case TRANSPORTATION:
                return "🚗";
            case ENTERTAINMENT:
                return "🎬";
            case SHOPPING:
                return "🛍️";
            case BILLS_UTILITIES:
                return "💡";
            case HEALTHCARE:
                return "🏥";
            case TRAVEL:
                return "✈️";
            case EDUCATION:
                return "📚";
            default:
                return "📦";
        }
    }
}
