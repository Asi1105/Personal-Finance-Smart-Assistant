package com.sydney.uni.backend.services;

import com.sydney.uni.backend.dto.DepositRequest;
import com.sydney.uni.backend.entity.Account;
import com.sydney.uni.backend.entity.Transaction;
import com.sydney.uni.backend.entity.TransactionType;
import com.sydney.uni.backend.entity.User;
import com.sydney.uni.backend.repository.AccountRepository;
import com.sydney.uni.backend.repository.TransactionRepository;
import com.sydney.uni.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class DepositService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public DepositService(AccountRepository accountRepository,
                         TransactionRepository transactionRepository,
                         UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Account processDeposit(Long userId, DepositRequest depositRequest) {
        // Find or create account for the user
        Account account = findOrCreateAccount(userId);
        
        // Update account balance
        Double currentBalance = account.getBalance() != null ? account.getBalance() : 0.0;
        account.setBalance(currentBalance + depositRequest.getAmount());
        
        // Save updated account
        Account savedAccount = accountRepository.save(account);
        
        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.IN);
        transaction.setAmount(depositRequest.getAmount());
        transaction.setDate(LocalDate.now());
        transaction.setDetail("Deposit");
        transaction.setNote(depositRequest.getDescription());
        transaction.setAccount(savedAccount);
        // For IN transactions (deposits), expenseCategory should be null
        transaction.setExpenseCategory(null);
        
        transactionRepository.save(transaction);
        
        return savedAccount;
    }

    private Account findOrCreateAccount(Long userId) {
        // Try to find existing account for the user
        List<Account> userAccounts = accountRepository.findByUserId(userId);
        
        if (!userAccounts.isEmpty()) {
            // Use the first account found
            return userAccounts.get(0);
        }
        
        // Create new account with default values
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Account newAccount = new Account();
        newAccount.setUser(user);
        newAccount.setBalance(0.0);
        newAccount.setSaved(0.0);
        
        return accountRepository.save(newAccount);
    }
}
