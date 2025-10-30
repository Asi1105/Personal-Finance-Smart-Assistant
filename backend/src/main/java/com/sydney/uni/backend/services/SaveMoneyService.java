package com.sydney.uni.backend.services;

import com.sydney.uni.backend.dto.SaveMoneyRequest;
import com.sydney.uni.backend.dto.UnsaveMoneyRequest;
import com.sydney.uni.backend.entity.Account;
import com.sydney.uni.backend.entity.SavingAction;
import com.sydney.uni.backend.entity.SavingLog;
import com.sydney.uni.backend.entity.User;
import com.sydney.uni.backend.repository.AccountRepository;
import com.sydney.uni.backend.repository.SavingLogRepository;
import com.sydney.uni.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SaveMoneyService {

    private final AccountRepository accountRepository;
    private final SavingLogRepository savingLogRepository;
    private final UserRepository userRepository;

    public SaveMoneyService(AccountRepository accountRepository, SavingLogRepository savingLogRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.savingLogRepository = savingLogRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Account saveMoney(Long userId, SaveMoneyRequest saveMoneyRequest) {
        // Find user's primary account
        List<Account> userAccounts = accountRepository.findByUserId(userId);
        if (userAccounts.isEmpty()) {
            throw new RuntimeException("No account found for user");
        }
        
        Account account = userAccounts.get(0); // Use first account
        
        // Check if account has sufficient balance
        if (account.getBalance() < saveMoneyRequest.getAmount()) {
            throw new RuntimeException("Insufficient balance to save");
        }
        
        // Mark money as saved without reducing balance
        // This is just a way to track how much of the balance is "saved" for goals
        Double currentSaved = account.getSaved() != null ? account.getSaved() : 0.0;
        account.setSaved(currentSaved + saveMoneyRequest.getAmount());
        // Note: balance remains unchanged - we're just marking part of it as "saved"
        
        // Save updated account
        Account savedAccount = accountRepository.save(account);
        
        // Create saving log record
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        SavingLog savingLog = new SavingLog();
        savingLog.setAction(SavingAction.SAVE);
        savingLog.setAmount(saveMoneyRequest.getAmount());
        savingLog.setDescription(saveMoneyRequest.getDescription() != null ? 
            saveMoneyRequest.getDescription() : "Money marked as saved");
        savingLog.setTimestamp(LocalDateTime.now());
        savingLog.setUser(user);
        savingLog.setAccount(savedAccount);
        
        savingLogRepository.save(savingLog);
        
        return savedAccount;
    }

    @Transactional
    public Account unsaveMoney(Long userId, UnsaveMoneyRequest unsaveMoneyRequest) {
        // Find user's primary account
        List<Account> userAccounts = accountRepository.findByUserId(userId);
        if (userAccounts.isEmpty()) {
            throw new RuntimeException("No account found for user");
        }
        
        Account account = userAccounts.get(0); // Use first account
        
        // Check if account has sufficient saved amount
        Double currentSaved = account.getSaved() != null ? account.getSaved() : 0.0;
        if (currentSaved < unsaveMoneyRequest.getAmount()) {
            throw new RuntimeException("Insufficient saved amount to unsave");
        }
        
        // Unmark money as saved
        account.setSaved(currentSaved - unsaveMoneyRequest.getAmount());
        
        // Save updated account
        Account savedAccount = accountRepository.save(account);
        
        // Create saving log record
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        SavingLog savingLog = new SavingLog();
        savingLog.setAction(SavingAction.UNSAVE);
        savingLog.setAmount(unsaveMoneyRequest.getAmount());
        savingLog.setDescription(unsaveMoneyRequest.getDescription() != null ? 
            unsaveMoneyRequest.getDescription() : "Money unmarked as saved");
        savingLog.setTimestamp(LocalDateTime.now());
        savingLog.setUser(user);
        savingLog.setAccount(savedAccount);
        
        savingLogRepository.save(savingLog);
        
        return savedAccount;
    }

    public List<SavingLog> getSavingLogs(Long userId) {
        return savingLogRepository.findByUserIdOrderByTimestampDesc(userId);
    }
}
