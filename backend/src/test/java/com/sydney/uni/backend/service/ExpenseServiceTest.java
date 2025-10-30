package com.sydney.uni.backend.service;

import com.sydney.uni.backend.dto.ExpenseRequest;
import com.sydney.uni.backend.entity.*;
import com.sydney.uni.backend.repository.AccountRepository;
import com.sydney.uni.backend.repository.TransactionRepository;
import com.sydney.uni.backend.repository.UserRepository;
import com.sydney.uni.backend.services.ExpenseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ExpenseService expenseService;

    private ExpenseRequest expenseRequest;
    private Account mockAccount;
    private Transaction mockTransaction;
    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("Leo Liu");

        mockAccount = new Account();
        mockAccount.setId(1L);
        mockAccount.setUser(mockUser);
        mockAccount.setBalance(500.0);
        mockAccount.setSaved(100.0);

        expenseRequest = new ExpenseRequest();
        expenseRequest.setDescription("Dinner");
        expenseRequest.setCategory("Food & Dining");
        expenseRequest.setAmount(100.0);
        expenseRequest.setDate(LocalDate.now());
        expenseRequest.setNotes("Weekend dinner");

        mockTransaction = new Transaction();
        mockTransaction.setId(1L);
        mockTransaction.setAccount(mockAccount);
        mockTransaction.setAmount(100.0);
        mockTransaction.setType(TransactionType.OUT);
        mockTransaction.setDetail("Dinner");
    }

    // Add Expense - Success
    @Test
    void testAddExpense_Success() {
        when(accountRepository.findByUserId(1L)).thenReturn(List.of(mockAccount));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result = expenseService.addExpense(1L, expenseRequest);

        assertNotNull(result);
        assertEquals(TransactionType.OUT, result.getType());
        assertEquals(ExpenseCategory.FOOD_DINING, result.getExpenseCategory());
        verify(accountRepository).save(mockAccount);
        verify(transactionRepository).save(any(Transaction.class));
    }

    // Add Expense - Insufficient balance
    @Test
    void testAddExpense_InsufficientBalance() {
        mockAccount.setBalance(50.0);
        when(accountRepository.findByUserId(1L)).thenReturn(List.of(mockAccount));

        assertThrows(RuntimeException.class, () -> expenseService.addExpense(1L, expenseRequest));
    }

    // Get Expense By ID - Not Belong to User
    @Test
    void testGetExpenseById_NotBelongToUser() {
        Account anotherAccount = new Account();
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherAccount.setUser(anotherUser);
        mockTransaction.setAccount(anotherAccount);

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(mockTransaction));

        assertThrows(RuntimeException.class, () -> expenseService.getExpenseById(1L, 1L));
    }

    // Update Expense - Success
    @Test
    void testUpdateExpense_Success() {
        mockTransaction.setAccount(mockAccount);
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(mockTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        expenseRequest.setAmount(120.0);
        Transaction updated = expenseService.updateExpense(1L, 1L, expenseRequest);

        assertEquals(120.0, updated.getAmount());
        verify(accountRepository).save(mockAccount);
    }

    // Delete Expense - Success
    @Test
    void testDeleteExpense_Success() {
        mockTransaction.setAccount(mockAccount);
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(mockTransaction));

        expenseService.deleteExpense(1L, 1L);

        verify(accountRepository).save(mockAccount);
        verify(transactionRepository).delete(mockTransaction);
        assertEquals(600.0, mockAccount.getBalance()); // refunded
    }

    // Get User Expenses
    @Test
    void testGetUserExpenses() {
        when(transactionRepository.findByAccountUserIdAndTypeOrderByDateDesc(1L, TransactionType.OUT))
                .thenReturn(List.of(mockTransaction));

        List<Transaction> result = expenseService.getUserExpenses(1L);
        assertEquals(1, result.size());
        assertEquals(TransactionType.OUT, result.get(0).getType());
    }

    @Test
    void testFindOrCreatePrimaryAccount_WhenExists() {
        when(accountRepository.findByUserId(1L)).thenReturn(List.of(mockAccount));
        // invoke via public API addExpense to reach the branch
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));
        expenseService.addExpense(1L, expenseRequest);
        verify(accountRepository, atLeastOnce()).findByUserId(1L);
    }

    @Test
    void testFindOrCreatePrimaryAccount_WhenNotExistsCreates() {
        when(accountRepository.findByUserId(1L)).thenReturn(List.of());
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));
        expenseRequest.setAmount(0.0);
        expenseService.addExpense(1L, expenseRequest);
        verify(userRepository).findById(1L);
        verify(accountRepository, atLeastOnce()).save(any(Account.class));
    }

    @Test
    void testFindOrCreatePrimaryAccount_UserNotFound_throws() {
        when(accountRepository.findByUserId(1L)).thenReturn(List.of());
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> expenseService.addExpense(1L, expenseRequest));
    }

    @Test
    void testGetExpenseById_NotFound_throws() {
        when(transactionRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> expenseService.getExpenseById(999L, 1L));
    }

    @Test
    void testConvertStringToExpenseCategory_AllMappings() {
        // hit switch branches via update/add paths
        String[] names = new String[]{
                "Food & Dining","Transportation","Entertainment","Shopping",
                "Bills & Utilities","Healthcare","Travel","Education"
        };
        when(accountRepository.findByUserId(1L)).thenReturn(List.of(mockAccount));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));
        for (String n : names) {
            expenseRequest.setCategory(n);
            expenseRequest.setAmount(0.0);
            Transaction t = expenseService.addExpense(1L, expenseRequest);
            assertNotNull(t.getExpenseCategory());
        }
    }

    @Test
    void testConvertStringToExpenseCategory_NullAndEmpty() {
        when(accountRepository.findByUserId(1L)).thenReturn(List.of(mockAccount));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));
        expenseRequest.setCategory(null);
        Transaction t1 = expenseService.addExpense(1L, expenseRequest);
        assertNull(t1.getExpenseCategory());
        expenseRequest.setCategory("   ");
        Transaction t2 = expenseService.addExpense(1L, expenseRequest);
        assertNull(t2.getExpenseCategory());
    }
}
