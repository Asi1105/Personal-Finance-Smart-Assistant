package com.sydney.uni.backend.services;

import com.sydney.uni.backend.dto.*;
import com.sydney.uni.backend.entity.Transaction;
import com.sydney.uni.backend.entity.Budget;
import com.sydney.uni.backend.entity.TransactionType;
import com.sydney.uni.backend.entity.SavingLog;
import com.sydney.uni.backend.entity.SavingAction;
import com.sydney.uni.backend.repository.TransactionRepository;
import com.sydney.uni.backend.repository.BudgetRepository;
import com.sydney.uni.backend.repository.SavingLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ReportsService {

    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final SavingLogRepository savingLogRepository;
    
    // Category colors mapping
    private final Map<String, String> categoryColors = Map.of(
        "FOOD_DINING", "#ff6b6b",
        "TRANSPORTATION", "#4ecdc4", 
        "ENTERTAINMENT", "#45b7d1",
        "SHOPPING", "#f39c12",
        "BILLS_UTILITIES", "#e74c3c",
        "HEALTHCARE", "#27ae60",
        "TRAVEL", "#9b59b6",
        "EDUCATION", "#3498db",
        "OTHER", "#95a5a6"
    );
    
    public ReportsService(TransactionRepository transactionRepository, 
                         BudgetRepository budgetRepository,
                         SavingLogRepository savingLogRepository) {
        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
        this.savingLogRepository = savingLogRepository;
    }
    
    public ReportsDto getReportsData(Long userId, String period) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = calculateStartDate(endDate, period);
        
        // Get transactions for the period
        List<Transaction> transactions = transactionRepository.findByUserIdAndDateBetween(
            userId, startDate, endDate
        );
        
        // Get budgets
        List<Budget> budgets = budgetRepository.findByUserId(userId);
        
        // Get saving logs for the period
        List<SavingLog> savingLogs = savingLogRepository.findByUserIdOrderByTimestampDesc(userId);
        
        // Generate monthly data
        List<MonthlyDataDto> monthlyData = generateMonthlyData(transactions, savingLogs, startDate, endDate);
        
        // Generate category expenses
        List<CategoryExpenseDto> categoryExpenses = generateCategoryExpenses(transactions);
        
        // Generate budget comparison (use selected period)
        List<BudgetComparisonDto> budgetComparison = generateBudgetComparison(budgets, transactions, startDate, endDate);
        
        // Generate metrics
        ReportsMetricsDto metrics = generateMetrics(monthlyData);
        
        return new ReportsDto(monthlyData, categoryExpenses, budgetComparison, metrics);
    }
    
    private LocalDate calculateStartDate(LocalDate endDate, String period) {
        return switch (period) {
            case "6months" -> endDate.minusMonths(6).withDayOfMonth(1);
            case "year" -> endDate.withDayOfYear(1); // Start from January 1st of current year
            default -> endDate.minusMonths(6).withDayOfMonth(1); // Default to 6 months
        };
    }
    
    private List<MonthlyDataDto> generateMonthlyData(List<Transaction> transactions, 
                                                   List<SavingLog> savingLogs,
                                                   LocalDate startDate, LocalDate endDate) {
        Map<String, MonthlyDataDto> monthlyMap = new LinkedHashMap<>();
        
        // Initialize all months in the range - from startDate to endDate
        LocalDate current = startDate.withDayOfMonth(1);
        LocalDate endMonth = endDate.withDayOfMonth(1);
        
        // For 6 months period, ensure we only show exactly 6 months
        if (startDate.equals(endDate.minusMonths(6).withDayOfMonth(1))) {
            // This is a 6-month period, show exactly 6 months ending with current month
            endMonth = endDate.withDayOfMonth(1);
            current = endMonth.minusMonths(5); // Start from 6 months before end month
        }
        
        while (current.isBefore(endMonth) || current.isEqual(endMonth)) {
            String monthKey = current.format(DateTimeFormatter.ofPattern("MMM", Locale.ENGLISH));
            monthlyMap.put(monthKey, new MonthlyDataDto(monthKey, 0.0, 0.0, 0.0));
            current = current.plusMonths(1);
        }
        
        // Process transactions
        for (Transaction transaction : transactions) {
            String monthKey = transaction.getDate().format(DateTimeFormatter.ofPattern("MMM", Locale.ENGLISH));
            MonthlyDataDto monthlyData = monthlyMap.get(monthKey);
            
            if (monthlyData != null) {
                if (transaction.getType() == TransactionType.IN) {
                    monthlyData.setIncome(monthlyData.getIncome() + transaction.getAmount());
                } else {
                    monthlyData.setExpenses(monthlyData.getExpenses() + transaction.getAmount());
                }
            }
        }
        
        // Process saving logs to calculate actual savings
        for (SavingLog savingLog : savingLogs) {
            LocalDate logDate = savingLog.getTimestamp().toLocalDate();
            if (!logDate.isBefore(startDate) && !logDate.isAfter(endDate)) {
                String monthKey = logDate.format(DateTimeFormatter.ofPattern("MMM", Locale.ENGLISH));
                MonthlyDataDto monthlyData = monthlyMap.get(monthKey);
                
                if (monthlyData != null) {
                    if (savingLog.getAction() == SavingAction.SAVE) {
                        monthlyData.setSavings(monthlyData.getSavings() + savingLog.getAmount());
                    } else if (savingLog.getAction() == SavingAction.UNSAVE) {
                        monthlyData.setSavings(monthlyData.getSavings() - savingLog.getAmount());
                    }
                }
            }
        }
        
        return new ArrayList<>(monthlyMap.values());
    }
    
    private List<CategoryExpenseDto> generateCategoryExpenses(List<Transaction> transactions) {
        Map<String, Double> categoryTotals = new HashMap<>();
        final double[] totalExpenses = {0.0};
        
        // Calculate totals by category
        for (Transaction transaction : transactions) {
            if (transaction.getType() == TransactionType.OUT && 
                transaction.getExpenseCategory() != null) {
                String category = transaction.getExpenseCategory().name();
                categoryTotals.merge(category, transaction.getAmount(), Double::sum);
                totalExpenses[0] += transaction.getAmount();
            }
        }
        
        final double finalTotalExpenses = totalExpenses[0];
        
        // Convert to DTOs
        return categoryTotals.entrySet().stream()
            .map(entry -> {
                String category = convertCategoryName(entry.getKey());
                Double amount = entry.getValue();
                String color = categoryColors.getOrDefault(entry.getKey(), "#95a5a6");
                String percentage = finalTotalExpenses > 0 ? 
                    String.format("%.1f", (amount / finalTotalExpenses) * 100) : "0.0";
                
                return new CategoryExpenseDto(category, amount, color, percentage);
            })
            .sorted((a, b) -> Double.compare(b.getAmount(), a.getAmount()))
            .toList();
    }
    
    private List<BudgetComparisonDto> generateBudgetComparison(List<Budget> budgets, 
                                                             List<Transaction> transactions,
                                                             LocalDate startDate, LocalDate endDate) {
        Map<String, Double> spentByCategory = new HashMap<>();
        
        // Calculate spent amounts by category using enum names for the selected period
        for (Transaction transaction : transactions) {
            if (transaction.getType() == TransactionType.OUT && 
                transaction.getExpenseCategory() != null) {
                // Use enum name directly to match Budget.category format
                String category = transaction.getExpenseCategory().name();
                Double amount = transaction.getAmount();
                spentByCategory.merge(category, amount, Double::sum);
            }
        }
        
        // Calculate number of months in the period
        long monthsInPeriod = java.time.temporal.ChronoUnit.MONTHS.between(
            startDate.withDayOfMonth(1), 
            endDate.withDayOfMonth(1)
        ) + 1; // +1 to include both start and end months
        
        return budgets.stream()
            .map(budget -> {
                String category = convertCategoryName(budget.getCategory());
                // Budgeted amount for the entire period (monthly budget * number of months)
                Double budgeted = budget.getAmount() * monthsInPeriod;
                // Spent amount for the entire period
                Double spent = spentByCategory.getOrDefault(budget.getCategory(), 0.0);
                Double remaining = budgeted - spent;
                
                return new BudgetComparisonDto(category, budgeted, spent, remaining);
            })
            .toList();
    }
    
    private ReportsMetricsDto generateMetrics(List<MonthlyDataDto> monthlyData) {
        double totalIncome = monthlyData.stream().mapToDouble(MonthlyDataDto::getIncome).sum();
        double totalExpenses = monthlyData.stream().mapToDouble(MonthlyDataDto::getExpenses).sum();
        // Use savings from monthly data (period-specific savings)
        double totalSavings = monthlyData.stream().mapToDouble(MonthlyDataDto::getSavings).sum();
        double avgMonthlyExpenses = monthlyData.isEmpty() ? 0.0 : totalExpenses / monthlyData.size();
        double savingsRate = totalIncome > 0 ? (totalSavings / totalIncome) * 100 : 0.0;
        
        return new ReportsMetricsDto(totalIncome, totalExpenses, totalSavings, 
                                   avgMonthlyExpenses, savingsRate);
    }
    
    private String convertCategoryName(String categoryName) {
        return switch (categoryName) {
            case "FOOD_DINING" -> "Food & Dining";
            case "TRANSPORTATION" -> "Transportation";
            case "ENTERTAINMENT" -> "Entertainment";
            case "SHOPPING" -> "Shopping";
            case "BILLS_UTILITIES" -> "Bills & Utilities";
            case "HEALTHCARE" -> "Healthcare";
            case "TRAVEL" -> "Travel";
            case "EDUCATION" -> "Education";
            case "OTHER" -> "Other";
            default -> categoryName;
        };
    }
    
}