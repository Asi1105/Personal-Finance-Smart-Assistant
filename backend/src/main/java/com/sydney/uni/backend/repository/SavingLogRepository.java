package com.sydney.uni.backend.repository;

import com.sydney.uni.backend.entity.SavingLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavingLogRepository extends JpaRepository<SavingLog, Long> {
    List<SavingLog> findByUserIdOrderByTimestampDesc(Long userId);
    List<SavingLog> findByAccountIdOrderByTimestampDesc(Long accountId);
}
