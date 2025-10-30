package com.sydney.uni.backend.service;

import com.sydney.uni.backend.dto.DepositRequest;
import com.sydney.uni.backend.entity.Account;
import com.sydney.uni.backend.entity.Transaction;
import com.sydney.uni.backend.entity.TransactionType;
import com.sydney.uni.backend.entity.User;
import com.sydney.uni.backend.repository.AccountRepository;
import com.sydney.uni.backend.repository.TransactionRepository;
import com.sydney.uni.backend.repository.UserRepository;
import com.sydney.uni.backend.services.DepositService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepositServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DepositService depositService;

    private DepositRequest depositRequest;
    private Account mockAccount;
    private User mockUser;

    @BeforeEach
    void setup() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("Leo Liu");

        mockAccount = new Account();
        mockAccount.setId(1L);
        mockAccount.setUser(mockUser);
        mockAccount.setBalance(100.0);
        mockAccount.setSaved(0.0);

        depositRequest = new DepositRequest();
        depositRequest.setAmount(50.0);
        depositRequest.setDescription("Top-up");
    }

    @Test
    void testProcessDeposit_WithExistingAccount() {
        when(accountRepository.findByUserId(1L)).thenReturn(List.of(mockAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Account result = depositService.processDeposit(1L, depositRequest);

        assertNotNull(result);
        assertEquals(150.0, result.getBalance());
        verify(accountRepository, times(1)).save(mockAccount);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testProcessDeposit_WithNewAccount() {
        when(accountRepository.findByUserId(1L)).thenReturn(List.of());
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Account result = depositService.processDeposit(1L, depositRequest);

        assertNotNull(result);
        assertEquals(50.0, result.getBalance());
        verify(accountRepository, atLeastOnce()).save(any(Account.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void testProcessDeposit_UserNotFound() {
        when(accountRepository.findByUserId(1L)).thenReturn(List.of());
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                depositService.processDeposit(1L, depositRequest));

        assertEquals("User not found", ex.getMessage());
        verify(accountRepository, never()).save(mockAccount);
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void testFindOrCreateAccount_UsesExistingAccount() {
        when(accountRepository.findByUserId(1L)).thenReturn(List.of(mockAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Account result = depositService.processDeposit(1L, depositRequest);

        assertNotNull(result);
        assertEquals(mockUser, result.getUser());
        assertEquals(150.0, result.getBalance());
        verify(accountRepository).findByUserId(1L);
    }

    @Test
    void testProcessDeposit_SavesCorrectTransaction() {
        when(accountRepository.findByUserId(1L)).thenReturn(List.of(mockAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(mockAccount);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction tx = invocation.getArgument(0);
            assertEquals(TransactionType.IN, tx.getType());
            assertEquals(depositRequest.getAmount(), tx.getAmount());
            assertNull(tx.getExpenseCategory());
            assertEquals("Deposit", tx.getDetail());
            return tx;
        });

        depositService.processDeposit(1L, depositRequest);

        verify(transactionRepository).save(any(Transaction.class));
    }
}
