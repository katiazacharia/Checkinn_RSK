package com.project.checkinn.loyalty.account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;

public interface LoyaltyAccountRepo extends JpaRepository<LoyaltyAccount, Long> {
    Optional<LoyaltyAccount> findByUser_Id(Long userId);
    boolean existsByUser_Id(Long userId);
    List<LoyaltyAccount> findByUpdatedAtBeforeAndPointsGreaterThan(
            LocalDateTime cutoff, int points
    );
}

