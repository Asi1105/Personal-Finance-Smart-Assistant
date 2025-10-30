package com.sydney.uni.backend.service;

import com.sydney.uni.backend.dto.BudgetDto;
import com.sydney.uni.backend.dto.BudgetRequest;
import com.sydney.uni.backend.entity.*;
import com.sydney.uni.backend.repository.BudgetRepository;
import com.sydney.uni.backend.repository.TransactionRepository;
import com.sydney.uni.backend.repository.UserRepository;
import com.sydney.uni.backend.services.BudgetService;
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

class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private BudgetService budgetService;

    private User user;
    private Budget budget;
    private BudgetRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setName("John Doe");

        budget = new Budget();
        budget.setId(10L);
        budget.setUser(user);
        budget.setCategory("Food & Dining");
        budget.setPeriod("monthly");
        budget.setAmount(500.0);

        request = new BudgetRequest();
        request.setCategory("Food & Dining");
        request.setPeriod("monthly");
        request.setAmount(600.0);
    }

    // addBudget - create new budget
    @Test
    void testAddBudget_CreateNew() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(budgetRepository.findByUserIdAndCategoryAndPeriod(1L, "Food & Dining", "monthly"))
                .thenReturn(Optional.empty());
        when(budgetRepository.save(any(Budget.class))).thenReturn(budget);

        Budget result = budgetService.addBudget(1L, request);

        assertNotNull(result);
        verify(budgetRepository, times(1)).save(any(Budget.class));
    }

    // addBudget - update existing
    @Test
    void testAddBudget_UpdateExisting() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(budgetRepository.findByUserIdAndCategoryAndPeriod(1L, "Food & Dining", "monthly"))
                .thenReturn(Optional.of(budget));
        when(budgetRepository.save(any(Budget.class))).thenReturn(budget);

        Budget result = budgetService.addBudget(1L, request);

        assertEquals(600.0, result.getAmount());
        verify(budgetRepository, times(1)).save(budget);
    }

    // addBudget - user not found
    @Test
    void testAddBudget_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> budgetService.addBudget(1L, request));

        assertEquals("User not found", ex.getMessage());
    }

    //  getUserBudgets
    @Test
    void testGetUserBudgets() {
        when(budgetRepository.findByUserId(1L)).thenReturn(List.of(budget));

        List<Budget> result = budgetService.getUserBudgets(1L);

        assertEquals(1, result.size());
        assertEquals("Food & Dining", result.get(0).getCategory());
    }

    // getUserBudgetsWithSpending
    @Test
    void testGetUserBudgetsWithSpending() {
        Transaction tx = new Transaction();
        tx.setType(TransactionType.OUT);
        tx.setAmount(100.0);
        tx.setExpenseCategory(ExpenseCategory.FOOD_DINING);
        tx.setDate(LocalDate.now());

        Budget budget = new Budget();
        budget.setId(1L);
        budget.setCategory("FOOD_DINING");
        budget.setAmount(500.0);
        budget.setPeriod("monthly");

        when(budgetRepository.findByUserId(anyLong()))
                .thenReturn(List.of(budget));

        when(transactionRepository.findByAccountUserIdAndDateBetweenAndType(
                anyLong(),
                any(LocalDate.class),
                any(LocalDate.class),
                eq(TransactionType.OUT)
        )).thenReturn(List.of(tx));

        List<BudgetDto> result = budgetService.getUserBudgetsWithSpending(1L);

        assertEquals(1, result.size());
        assertEquals(100.0, result.get(0).getSpent());
        assertEquals(400.0, result.get(0).getRemaining());
        assertTrue(result.get(0).getUtilizationPercentage() > 0);
    }

    // getUserBudgetByPeriod
    @Test
    void testGetUserBudgetByPeriod() {
        when(budgetRepository.findByUserIdAndPeriod(1L, "monthly")).thenReturn(Optional.of(budget));

        Optional<Budget> result = budgetService.getUserBudgetByPeriod(1L, "monthly");

        assertTrue(result.isPresent());
        assertEquals("Food & Dining", result.get().getCategory());
    }

    // getBudgetById success
    @Test
    void testGetBudgetById_Success() {
        Transaction tx = new Transaction();
        tx.setType(TransactionType.OUT);
        tx.setAmount(200.0);
        tx.setExpenseCategory(ExpenseCategory.FOOD_DINING);
        tx.setDate(LocalDate.now());

        when(budgetRepository.findById(10L)).thenReturn(Optional.of(budget));
        when(transactionRepository.findByAccountUserIdAndDateBetweenAndType(eq(1L), any(), any(), eq(TransactionType.OUT)))
                .thenReturn(List.of(tx));

        BudgetDto dto = budgetService.getBudgetById(10L, 1L);

        assertNotNull(dto);
        assertEquals("Food & Dining", dto.getCategory());
    }

    //  getBudgetById - budget not found
    @Test
    void testGetBudgetById_NotFound() {
        when(budgetRepository.findById(10L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> budgetService.getBudgetById(10L, 1L));

        assertEquals("Budget not found", ex.getMessage());
    }

    // getBudgetById - not belong to user
    @Test
    void testGetBudgetById_NotBelongToUser() {
        User otherUser = new User();
        otherUser.setId(2L);
        budget.setUser(otherUser);

        when(budgetRepository.findById(10L)).thenReturn(Optional.of(budget));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> budgetService.getBudgetById(10L, 1L));

        assertEquals("Budget does not belong to user", ex.getMessage());
    }

    // updateBudget success
    @Test
    void testUpdateBudget_Success() {
        when(budgetRepository.findById(10L)).thenReturn(Optional.of(budget));
        when(budgetRepository.save(any(Budget.class))).thenReturn(budget);

        Budget result = budgetService.updateBudget(10L, 1L, request);

        assertEquals(600.0, result.getAmount());
        verify(budgetRepository, times(1)).save(any(Budget.class));
    }

    // updateBudget - not belong to user
    @Test
    void testUpdateBudget_NotBelongToUser() {
        User otherUser = new User();
        otherUser.setId(2L);
        budget.setUser(otherUser);
        when(budgetRepository.findById(10L)).thenReturn(Optional.of(budget));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> budgetService.updateBudget(10L, 1L, request));

        assertEquals("Budget does not belong to user", ex.getMessage());
    }

    //  updateBudget - not found
    @Test
    void testUpdateBudget_NotFound() {
        when(budgetRepository.findById(10L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> budgetService.updateBudget(10L, 1L, request));

        assertEquals("Budget not found", ex.getMessage());
    }

    //  deleteBudget success
    @Test
    void testDeleteBudget_Success() {
        when(budgetRepository.findById(10L)).thenReturn(Optional.of(budget));

        budgetService.deleteBudget(1L, 10L);

        verify(budgetRepository, times(1)).delete(budget);
    }

    // deleteBudget - not found
    @Test
    void testDeleteBudget_NotFound() {
        when(budgetRepository.findById(10L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> budgetService.deleteBudget(1L, 10L));

        assertEquals("Budget not found", ex.getMessage());
    }

    // deleteBudget - not belong to user
    @Test
    void testDeleteBudget_NotBelongToUser() {
        User otherUser = new User();
        otherUser.setId(2L);
        budget.setUser(otherUser);
        when(budgetRepository.findById(10L)).thenReturn(Optional.of(budget));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> budgetService.deleteBudget(1L, 10L));

        assertEquals("Budget does not belong to user", ex.getMessage());
    }
}
