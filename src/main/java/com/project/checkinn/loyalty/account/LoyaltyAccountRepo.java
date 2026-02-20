package com.project.checkinn.loyalty.account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoyaltyAccountRepo extends JpaRepository<LoyaltyAccount, Long> {
    Optional<LoyaltyAccount> findByUser_Id(Long userId);
    boolean existsByUser_Id(Long userId);
}

