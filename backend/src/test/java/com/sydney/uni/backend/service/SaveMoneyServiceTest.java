package com.sydney.uni.backend.service;

import com.sydney.uni.backend.dto.SaveMoneyRequest;
import com.sydney.uni.backend.dto.UnsaveMoneyRequest;
import com.sydney.uni.backend.entity.*;
import com.sydney.uni.backend.repository.AccountRepository;
import com.sydney.uni.backend.repository.SavingLogRepository;
import com.sydney.uni.backend.repository.UserRepository;
import com.sydney.uni.backend.services.SaveMoneyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SaveMoneyServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private SavingLogRepository savingLogRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SaveMoneyService saveMoneyService;

    private Account account;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setName("Test User");

        account = new Account();
        account.setId(1L);
        account.setBalance(100.0);
        account.setSaved(20.0);
        account.setUser(user);
    }

    // Test normal saving money flow
    @Test
    void testSaveMoney_Success() {
        SaveMoneyRequest request = new SaveMoneyRequest();
        request.setAmount(50.0);
        request.setDescription("Test Save");

        when(accountRepository.findByUserId(1L)).thenReturn(List.of(account));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        Account result = saveMoneyService.saveMoney(1L, request);

        assertNotNull(result);
        assertEquals(70.0, result.getSaved());
        verify(savingLogRepository, times(1)).save(any(SavingLog.class));
    }

    // Test insufficient balance error
    @Test
    void testSaveMoney_InsufficientBalance() {
        SaveMoneyRequest request = new SaveMoneyRequest();
        request.setAmount(200.0);
        when(accountRepository.findByUserId(1L)).thenReturn(List.of(account));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                saveMoneyService.saveMoney(1L, request));

        assertEquals("Insufficient balance to save", ex.getMessage());
        verify(savingLogRepository, never()).save(any());
    }

    // Test when no account found
    @Test
    void testSaveMoney_NoAccountFound() {
        SaveMoneyRequest request = new SaveMoneyRequest();
        request.setAmount(20.0);
        when(accountRepository.findByUserId(1L)).thenReturn(List.of());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                saveMoneyService.saveMoney(1L, request));

        assertEquals("No account found for user", ex.getMessage());
    }

    //  Test when user not found
    @Test
    void testSaveMoney_UserNotFound() {
        SaveMoneyRequest request = new SaveMoneyRequest();
        request.setAmount(10.0);
        when(accountRepository.findByUserId(1L)).thenReturn(List.of(account));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                saveMoneyService.saveMoney(1L, request));

        assertEquals("User not found", ex.getMessage());
    }

    // Test unsave money flow
    @Test
    void testUnsaveMoney_Success() {
        UnsaveMoneyRequest request = new UnsaveMoneyRequest();
        request.setAmount(10.0);
        request.setDescription("Withdraw Save");

        when(accountRepository.findByUserId(1L)).thenReturn(List.of(account));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        Account result = saveMoneyService.unsaveMoney(1L, request);

        assertNotNull(result);
        assertEquals(10.0, result.getSaved());
        verify(savingLogRepository, times(1)).save(any(SavingLog.class));
    }

    // Test unsave more than saved amount
    @Test
    void testUnsaveMoney_InsufficientSaved() {
        UnsaveMoneyRequest request = new UnsaveMoneyRequest();
        request.setAmount(50.0);

        when(accountRepository.findByUserId(1L)).thenReturn(List.of(account));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                saveMoneyService.unsaveMoney(1L, request));

        assertEquals("Insufficient saved amount to unsave", ex.getMessage());
        verify(savingLogRepository, never()).save(any());
    }

    // Test unsave when no account
    @Test
    void testUnsaveMoney_NoAccountFound() {
        UnsaveMoneyRequest request = new UnsaveMoneyRequest();
        request.setAmount(10.0);
        when(accountRepository.findByUserId(1L)).thenReturn(List.of());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                saveMoneyService.unsaveMoney(1L, request));

        assertEquals("No account found for user", ex.getMessage());
    }

    // Test user not found during unsave
    @Test
    void testUnsaveMoney_UserNotFound() {
        UnsaveMoneyRequest request = new UnsaveMoneyRequest();
        request.setAmount(10.0);

        when(accountRepository.findByUserId(1L)).thenReturn(List.of(account));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                saveMoneyService.unsaveMoney(1L, request));

        assertEquals("User not found", ex.getMessage());
    }

    // Test get saving logs
    @Test
    void testGetSavingLogs() {
        SavingLog log = new SavingLog();
        log.setAmount(10.0);
        when(savingLogRepository.findByUserIdOrderByTimestampDesc(1L)).thenReturn(List.of(log));

        List<SavingLog> logs = saveMoneyService.getSavingLogs(1L);

        assertEquals(1, logs.size());
        assertEquals(10.0, logs.get(0).getAmount());
    }
}
