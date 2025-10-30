package com.sydney.uni.backend.repository;

import com.sydney.uni.backend.entity.Transaction;
import com.sydney.uni.backend.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    @Query("SELECT t FROM Transaction t WHERE t.account.user.id = :userId AND t.date BETWEEN :startDate AND :endDate AND t.type = :type")
    List<Transaction> findByAccountUserIdAndDateBetweenAndType(@Param("userId") Long userId, 
                                                              @Param("startDate") LocalDate startDate, 
                                                              @Param("endDate") LocalDate endDate, 
                                                              @Param("type") TransactionType type);
    
    @Query("SELECT t FROM Transaction t WHERE t.account.user.id = :userId ORDER BY t.date DESC")
    List<Transaction> findTop10ByAccountUserIdOrderByDateDesc(@Param("userId") Long userId);
    
    @Query("SELECT t FROM Transaction t WHERE t.account.user.id = :userId AND t.type = :type ORDER BY t.date DESC")
    List<Transaction> findByAccountUserIdAndTypeOrderByDateDesc(@Param("userId") Long userId, @Param("type") TransactionType type);
    
    @Query("SELECT t FROM Transaction t WHERE t.account.user.id = :userId AND t.date BETWEEN :startDate AND :endDate ORDER BY t.date DESC")
    List<Transaction> findByUserIdAndDateBetween(@Param("userId") Long userId, 
                                                @Param("startDate") LocalDate startDate, 
                                                @Param("endDate") LocalDate endDate);
}
