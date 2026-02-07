package com.project.checkinn.loyalty;

import com.project.checkinn.common.LoyaltyTransactionType;
import com.project.checkinn.loyalty.account.LoyaltyAccount;
import com.project.checkinn.loyalty.account.LoyaltyAccountRepo;
import com.project.checkinn.loyalty.account.LoyaltyAccountResponse;
import com.project.checkinn.loyalty.dto.EarnRequest;
import com.project.checkinn.loyalty.dto.RedeemRequest;
import com.project.checkinn.loyalty.transaction.LoyaltyTransaction;
import com.project.checkinn.loyalty.transaction.LoyaltyTransactionRepo;
import com.project.checkinn.loyalty.transaction.LoyaltyTransactionResponse;
import com.project.checkinn.user.profile.User;

import jakarta.persistence.EntityManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LoyaltyServiceImpl implements LoyaltyService {

    private final LoyaltyAccountRepo accountRepo;
    private final LoyaltyTransactionRepo txRepo;
    private final EntityManager entityManager;

    public LoyaltyServiceImpl(LoyaltyAccountRepo accountRepo,
                              LoyaltyTransactionRepo txRepo,
                              EntityManager entityManager) {
        this.accountRepo = accountRepo;
        this.txRepo = txRepo;
        this.entityManager = entityManager;
    }

    @Override
    public LoyaltyAccountResponse getOrCreate(Long userId) {
        if (userId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");

        LoyaltyAccount acc = accountRepo.findByUser_Id(userId).orElseGet(() -> {
            User userRef = entityManager.getReference(User.class, userId);
            LoyaltyAccount a = new LoyaltyAccount();
            a.setUser(userRef);
            a.setPoints(0);
            a.setUpdatedAt(LocalDateTime.now());
            return accountRepo.save(a);
        });

        return new LoyaltyAccountResponse(acc);
    }

    @Override
    public LoyaltyAccountResponse earn(EarnRequest request) {
        if (request == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request is required");
        if (request.getUserId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        if (request.getPoints() <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "points must be > 0");

        LoyaltyAccount acc = accountRepo.findByUser_Id(request.getUserId()).orElseGet(() -> {
            User userRef = entityManager.getReference(User.class, request.getUserId());
            LoyaltyAccount a = new LoyaltyAccount();
            a.setUser(userRef);
            a.setPoints(0);
            a.setUpdatedAt(LocalDateTime.now());
            return accountRepo.save(a);
        });

        acc.setPoints(acc.getPoints() + request.getPoints());
        acc.setUpdatedAt(LocalDateTime.now());
        accountRepo.save(acc);

        User userRef = entityManager.getReference(User.class, request.getUserId());
        LoyaltyTransaction tx = new LoyaltyTransaction();
        tx.setUser(userRef);
        tx.setType(LoyaltyTransactionType.EARN);
        tx.setPoints(request.getPoints());
        tx.setNote(request.getNote());
        tx.setCreatedAt(LocalDateTime.now());
        txRepo.save(tx);

        return new LoyaltyAccountResponse(acc);
    }

    @Override
    public LoyaltyAccountResponse redeem(RedeemRequest request) {
        if (request == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request is required");
        if (request.getUserId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        if (request.getPoints() <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "points must be > 0");

        LoyaltyAccount acc = accountRepo.findByUser_Id(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loyalty account not found"));

        if (acc.getPoints() < request.getPoints())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough points");

        acc.setPoints(acc.getPoints() - request.getPoints());
        acc.setUpdatedAt(LocalDateTime.now());
        accountRepo.save(acc);

        User userRef = entityManager.getReference(User.class, request.getUserId());
        LoyaltyTransaction tx = new LoyaltyTransaction();
        tx.setUser(userRef);
        tx.setType(LoyaltyTransactionType.REDEEM);
        tx.setPoints(-request.getPoints());
        tx.setNote(request.getNote());
        tx.setCreatedAt(LocalDateTime.now());
        txRepo.save(tx);

        return new LoyaltyAccountResponse(acc);
    }

    @Override
    public List<LoyaltyTransactionResponse> history(Long userId) {
        if (userId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");

        return txRepo.findByUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(LoyaltyTransactionResponse::new)
                .toList();
    }
}