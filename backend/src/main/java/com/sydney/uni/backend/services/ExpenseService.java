package com.sydney.uni.backend.services;

import com.sydney.uni.backend.dto.ExpenseRequest;
import com.sydney.uni.backend.entity.Account;
import com.sydney.uni.backend.entity.Transaction;
import com.sydney.uni.backend.entity.TransactionType;
import com.sydney.uni.backend.entity.User;
import com.sydney.uni.backend.repository.AccountRepository;
import com.sydney.uni.backend.repository.TransactionRepository;
import com.sydney.uni.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExpenseService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public ExpenseService(AccountRepository accountRepository,
                         TransactionRepository transactionRepository,
                         UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Transaction addExpense(Long userId, ExpenseRequest expenseRequest) {
        // Find user's primary account (first account or create one)
        Account account = findOrCreatePrimaryAccount(userId);
        
        // Check if account has sufficient balance
        if (account.getBalance() < expenseRequest.getAmount()) {
            throw new RuntimeException("Insufficient balance");
        }
        
        // Update account balance
        account.setBalance(account.getBalance() - expenseRequest.getAmount());
        accountRepository.save(account);
        
        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.OUT);
        transaction.setAmount(expenseRequest.getAmount());
        transaction.setDate(expenseRequest.getDate());
        
        // Convert string category to ExpenseCategory enum
        com.sydney.uni.backend.entity.ExpenseCategory expenseCategory = convertStringToExpenseCategory(expenseRequest.getCategory());
        transaction.setExpenseCategory(expenseCategory);
        transaction.setDetail(expenseRequest.getDescription());
        transaction.setNote(expenseRequest.getNotes());
        transaction.setAccount(account);
        
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getUserExpenses(Long userId) {
        return transactionRepository.findByAccountUserIdAndTypeOrderByDateDesc(userId, TransactionType.OUT);
    }

    public Transaction getExpenseById(Long expenseId, Long userId) {
        Transaction transaction = transactionRepository.findById(expenseId)
            .orElseThrow(() -> new RuntimeException("Expense not found"));
        
        // Check if the transaction belongs to the user
        if (!transaction.getAccount().getUser().getId().equals(userId)) {
            throw new RuntimeException("Expense does not belong to user");
        }
        
        return transaction;
    }

    @Transactional
    public Transaction updateExpense(Long expenseId, Long userId, ExpenseRequest expenseRequest) {
        Transaction transaction = getExpenseById(expenseId, userId);
        
        // Calculate the difference in amount
        double oldAmount = transaction.getAmount();
        double newAmount = expenseRequest.getAmount();
        double difference = newAmount - oldAmount;
        
        // Update account balance
        Account account = transaction.getAccount();
        if (difference > 0 && account.getBalance() < difference) {
            throw new RuntimeException("Insufficient balance for the increase");
        }
        
        account.setBalance(account.getBalance() - difference);
        accountRepository.save(account);
        
        // Update transaction
        transaction.setAmount(expenseRequest.getAmount());
        transaction.setDate(expenseRequest.getDate());
        transaction.setDetail(expenseRequest.getDescription());
        transaction.setNote(expenseRequest.getNotes());
        transaction.setExpenseCategory(convertStringToExpenseCategory(expenseRequest.getCategory()));
        
        return transactionRepository.save(transaction);
    }

    @Transactional
    public void deleteExpense(Long expenseId, Long userId) {
        Transaction transaction = getExpenseById(expenseId, userId);
        
        // Refund the amount to account balance
        Account account = transaction.getAccount();
        account.setBalance(account.getBalance() + transaction.getAmount());
        accountRepository.save(account);
        
        // Delete the transaction
        transactionRepository.delete(transaction);
    }

    private Account findOrCreatePrimaryAccount(Long userId) {
        List<Account> userAccounts = accountRepository.findByUserId(userId);
        
        if (!userAccounts.isEmpty()) {
            return userAccounts.get(0); // Return first account
        }
        
        // Create a default account if none exists
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Account defaultAccount = new Account();
        defaultAccount.setUser(user);
        defaultAccount.setBalance(0.0);
        defaultAccount.setSaved(0.0);
        
        return accountRepository.save(defaultAccount);
    }

    private com.sydney.uni.backend.entity.ExpenseCategory convertStringToExpenseCategory(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            return null;
        }
        
        // Map frontend category names to enum values
        switch (categoryName.toLowerCase().trim()) {
            case "food & dining":
                return com.sydney.uni.backend.entity.ExpenseCategory.FOOD_DINING;
            case "transportation":
                return com.sydney.uni.backend.entity.ExpenseCategory.TRANSPORTATION;
            case "entertainment":
                return com.sydney.uni.backend.entity.ExpenseCategory.ENTERTAINMENT;
            case "shopping":
                return com.sydney.uni.backend.entity.ExpenseCategory.SHOPPING;
            case "bills & utilities":
                return com.sydney.uni.backend.entity.ExpenseCategory.BILLS_UTILITIES;
            case "healthcare":
                return com.sydney.uni.backend.entity.ExpenseCategory.HEALTHCARE;
            case "travel":
                return com.sydney.uni.backend.entity.ExpenseCategory.TRAVEL;
            case "education":
                return com.sydney.uni.backend.entity.ExpenseCategory.EDUCATION;
            default:
                return null; // Unknown category
        }
    }
}
