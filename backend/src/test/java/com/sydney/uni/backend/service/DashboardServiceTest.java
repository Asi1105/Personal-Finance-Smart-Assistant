package com.sydney.uni.backend.service;

import com.sydney.uni.backend.dto.DashboardStatsDto;
import com.sydney.uni.backend.dto.TransactionDto;
import com.sydney.uni.backend.entity.*;
import com.sydney.uni.backend.repository.*;
import com.sydney.uni.backend.services.DashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DashboardServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private SaveGoalRepository saveGoalRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetDashboardStats_WithAllData() {
        Long userId = 1L;

        // Mock accounts
        Account account = new Account();
        account.setBalance(1000.0);
        account.setSaved(200.0);
        when(accountRepository.findByUserId(userId)).thenReturn(List.of(account));

        // Mock transactions (current month and last month)
        Transaction currentTx = new Transaction();
        currentTx.setAmount(300.0);
        currentTx.setType(TransactionType.OUT);
        currentTx.setDate(LocalDate.now());

        Transaction lastTx = new Transaction();
        lastTx.setAmount(200.0);
        lastTx.setType(TransactionType.OUT);
        lastTx.setDate(LocalDate.now().minusMonths(1));

        when(transactionRepository.findByAccountUserIdAndDateBetweenAndType(
                eq(userId), any(), any(), eq(TransactionType.OUT)))
                .thenReturn(List.of(currentTx))
                .thenReturn(List.of(lastTx));

        // Mock budgets
        Budget monthlyBudget = new Budget();
        monthlyBudget.setAmount(1000.0);
        monthlyBudget.setPeriod("monthly");
        when(budgetRepository.findByUserId(userId)).thenReturn(List.of(monthlyBudget));

        // Mock save goal
        SaveGoal saveGoal = new SaveGoal();
        saveGoal.setTargetAmount(1000.0);
        when(saveGoalRepository.findByUserId(userId)).thenReturn(Optional.of(saveGoal));

        // Execute
        DashboardStatsDto stats = dashboardService.getDashboardStats(userId);

        // Verify and assert
        assertNotNull(stats);
        assertEquals(1000.0, stats.getTotalBalance());
        assertEquals(200.0, stats.getSaved());
        assertEquals(300.0, stats.getMonthlySpending());
        assertTrue(stats.getBudgetLeft() <= 1000.0);
        assertTrue(stats.getSavingsProgress() > 0);
        assertTrue(stats.getMonthlySpendingChange() >= 0);
        assertTrue(stats.getHasSavingsGoal());
    }

    @Test
    void testGetDashboardStats_NoBudgetsOrGoals() {
        Long userId = 2L;

        Account account = new Account();
        account.setBalance(0.0);
        account.setSaved(0.0);
        when(accountRepository.findByUserId(userId)).thenReturn(List.of(account));

        when(transactionRepository.findByAccountUserIdAndDateBetweenAndType(anyLong(), any(), any(), any()))
                .thenReturn(List.of());
        when(budgetRepository.findByUserId(userId)).thenReturn(List.of());
        when(saveGoalRepository.findByUserId(userId)).thenReturn(Optional.empty());

        DashboardStatsDto stats = dashboardService.getDashboardStats(userId);

        assertNotNull(stats);
        assertEquals(0.0, stats.getBudgetLeft());
        assertEquals(0.0, stats.getSavingsProgress());
        assertFalse(stats.getHasSavingsGoal());
    }

    @Test
    void testGetRecentTransactions() {
        Long userId = 3L;

        Transaction t1 = new Transaction();
        t1.setId(1L);
        t1.setType(TransactionType.OUT);
        t1.setDate(LocalDate.now());
        t1.setExpenseCategory(ExpenseCategory.SHOPPING);
        t1.setDetail("Groceries");
        t1.setAmount(50.0);
        t1.setNote("Supermarket");

        when(transactionRepository.findTop10ByAccountUserIdOrderByDateDesc(userId))
                .thenReturn(List.of(t1));

        List<TransactionDto> dtos = dashboardService.getRecentTransactions(userId, 5);

        assertEquals(1, dtos.size());
        assertEquals("Groceries", dtos.get(0).getDetail());
        assertEquals("Shopping", dtos.get(0).getCategoryDisplayName());
        assertEquals("🛍️", dtos.get(0).getIcon());
    }

    @Test
    void testConvertToTransactionDto_ForDeposit() {
        Transaction deposit = new Transaction();
        deposit.setType(TransactionType.IN);
        deposit.setAmount(500.0);
        deposit.setDetail("Deposit");
        deposit.setDate(LocalDate.now());

        TransactionDto dto = dashboardService.convertToTransactionDto(deposit);

        assertEquals("Deposit", dto.getCategoryDisplayName());
        assertEquals("💰", dto.getIcon());
    }

    @Test
    void testConvertToTransactionDto_ExpenseWithoutCategory_UseOther() {
        Transaction t = new Transaction();
        t.setType(TransactionType.OUT);
        t.setAmount(20.0);
        t.setDetail("Misc");
        t.setDate(LocalDate.now());

        TransactionDto dto = dashboardService.convertToTransactionDto(t);
        assertEquals("Other", dto.getCategoryDisplayName());
        assertEquals("💰", dto.getIcon());
    }

    @Test
    void testMonthlySpendingChange_BothZero() {
        Long userId = 9L;
        // accounts
        Account account = new Account();
        when(accountRepository.findByUserId(userId)).thenReturn(List.of(account));
        // current & last month both empty
        when(transactionRepository.findByAccountUserIdAndDateBetweenAndType(eq(userId), any(), any(), eq(TransactionType.OUT)))
                .thenReturn(List.of())
                .thenReturn(List.of());
        when(budgetRepository.findByUserId(userId)).thenReturn(List.of());
        when(saveGoalRepository.findByUserId(userId)).thenReturn(Optional.empty());

        DashboardStatsDto stats = dashboardService.getDashboardStats(userId);
        assertEquals(0.0, stats.getMonthlySpendingChange());
    }

    @Test
    void testMonthlySpendingChange_LastZeroCurrentPositive_100() {
        Long userId = 10L;
        Account account = new Account();
        when(accountRepository.findByUserId(userId)).thenReturn(List.of(account));
        Transaction current = new Transaction();
        current.setType(TransactionType.OUT);
        current.setAmount(50.0);
        current.setDate(LocalDate.now());
        when(transactionRepository.findByAccountUserIdAndDateBetweenAndType(eq(userId), any(), any(), eq(TransactionType.OUT)))
                .thenReturn(List.of(current))
                .thenReturn(List.of());
        when(budgetRepository.findByUserId(userId)).thenReturn(List.of());
        when(saveGoalRepository.findByUserId(userId)).thenReturn(Optional.empty());

        DashboardStatsDto stats = dashboardService.getDashboardStats(userId);
        assertEquals(100.0, stats.getMonthlySpendingChange());
    }

    @Test
    void testSavingsGoalPresentTargetZero_ProgressZero() {
        Long userId = 11L;
        Account account = new Account();
        account.setSaved(0.0);
        when(accountRepository.findByUserId(userId)).thenReturn(List.of(account));
        when(transactionRepository.findByAccountUserIdAndDateBetweenAndType(eq(userId), any(), any(), eq(TransactionType.OUT)))
                .thenReturn(List.of())
                .thenReturn(List.of());
        when(budgetRepository.findByUserId(userId)).thenReturn(List.of());
        SaveGoal goal = new SaveGoal();
        goal.setTargetAmount(0.0);
        when(saveGoalRepository.findByUserId(userId)).thenReturn(Optional.of(goal));

        DashboardStatsDto stats = dashboardService.getDashboardStats(userId);
        assertTrue(stats.getHasSavingsGoal());
        assertEquals(0.0, stats.getSavingsGoal());
        assertEquals(0.0, stats.getSavingsProgress());
    }

    @Test
    void testGetRecentTransactions_RespectsLimit() {
        Long userId = 12L;
        Transaction t1 = new Transaction(); t1.setId(1L); t1.setType(TransactionType.OUT); t1.setDate(LocalDate.now());
        Transaction t2 = new Transaction(); t2.setId(2L); t2.setType(TransactionType.OUT); t2.setDate(LocalDate.now());
        when(transactionRepository.findTop10ByAccountUserIdOrderByDateDesc(userId)).thenReturn(List.of(t1, t2));
        List<TransactionDto> dtos = dashboardService.getRecentTransactions(userId, 1);
        assertEquals(1, dtos.size());
    }

    @Test
    void testGetCategoryMapping_AllEnum() {
        for (ExpenseCategory c : ExpenseCategory.values()) {
            Transaction t = new Transaction();
            t.setType(TransactionType.OUT);
            t.setDate(LocalDate.now());
            t.setExpenseCategory(c);
            TransactionDto dto = dashboardService.convertToTransactionDto(t);

            switch (c) {
                case FOOD_DINING -> {
                    assertEquals("Food & Dining", dto.getCategoryDisplayName());
                    assertEquals("🍕", dto.getIcon());
                }
                case TRANSPORTATION -> {
                    assertEquals("Transportation", dto.getCategoryDisplayName());
                    assertEquals("🚗", dto.getIcon());
                }
                case ENTERTAINMENT -> {
                    assertEquals("Entertainment", dto.getCategoryDisplayName());
                    assertEquals("🎬", dto.getIcon());
                }
                case SHOPPING -> {
                    assertEquals("Shopping", dto.getCategoryDisplayName());
                    assertEquals("🛍️", dto.getIcon());
                }
                case BILLS_UTILITIES -> {
                    assertEquals("Bills & Utilities", dto.getCategoryDisplayName());
                    assertEquals("💡", dto.getIcon());
                }
                case HEALTHCARE -> {
                    assertEquals("Healthcare", dto.getCategoryDisplayName());
                    assertEquals("🏥", dto.getIcon());
                }
                case TRAVEL -> {
                    assertEquals("Travel", dto.getCategoryDisplayName());
                    assertEquals("✈️", dto.getIcon());
                }
                case EDUCATION -> {
                    assertEquals("Education", dto.getCategoryDisplayName());
                    assertEquals("📚", dto.getIcon());
                }
                case OTHER -> {
                    assertEquals("Other", dto.getCategoryDisplayName());
                    assertEquals("📦", dto.getIcon());
                }
            }
        }
    }
}
