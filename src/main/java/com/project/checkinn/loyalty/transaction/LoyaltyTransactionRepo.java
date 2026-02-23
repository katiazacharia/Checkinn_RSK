package com.project.checkinn.loyalty.transaction;

import com.project.checkinn.loyalty.transaction.LoyaltyTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface LoyaltyTransactionRepo extends JpaRepository<LoyaltyTransaction, Long>,
        JpaSpecificationExecutor<LoyaltyTransaction> {
    List<LoyaltyTransaction> findByUser_IdOrderByCreatedAtDesc(Long userId);
}
