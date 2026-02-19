package com.project.checkinn.loyalty;

import com.project.checkinn.common.LoyaltyTransactionType;
import com.project.checkinn.loyalty.account.LoyaltyAccount;
import com.project.checkinn.loyalty.account.LoyaltyAccountRepo;
import com.project.checkinn.loyalty.account.LoyaltyAccountResponse;
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
    private final LoyaltyTransactionRepo transactionRepo;
    private final EntityManager entityManager;

    public LoyaltyServiceImpl(LoyaltyAccountRepo accountRepo,
                              LoyaltyTransactionRepo transactionRepo,
                              EntityManager entityManager) {
        this.accountRepo = accountRepo;
        this.transactionRepo = transactionRepo;
        this.entityManager = entityManager;
    }

    @Override
    public LoyaltyAccountResponse getAccount(Long userId) {
        if (userId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");

        LoyaltyAccount acc = accountRepo.findByUser_Id(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "loyalty account not found"));

        return new LoyaltyAccountResponse(acc);
    }

    @Override
    public LoyaltyAccountResponse earn(EarnRequest request) {
        if (request == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request is required");
        if (request.getUserId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        if (request.getPoints() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "points must be > 0");
        LoyaltyAccount acc = accountRepo.findByUser_Id(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "loyalty account not found"));

        acc.setPoints(acc.getPoints() + request.getPoints());
        acc.setUpdatedAt(LocalDateTime.now());
        accountRepo.save(acc);

        LoyaltyTransaction tx = new LoyaltyTransaction();
        tx.setUser(entityManager.getReference(User.class, request.getUserId()));
        tx.setType(LoyaltyTransactionType.EARN);
        tx.setPoints(request.getPoints()); // + earn
        tx.setNote(request.getNote());
        tx.setCreatedAt(LocalDateTime.now());
        transactionRepo.save(tx);

        return new LoyaltyAccountResponse(acc);
    }

    @Override
    public LoyaltyAccountResponse redeem(RedeemRequest request) {
        if (request == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request is required");
        if (request.getUserId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        if (request.getPoints() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "points must be > 0");

        LoyaltyAccount acc = accountRepo.findByUser_Id(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "loyalty account not found"));

        int after = acc.getPoints() - request.getPoints();
        if (after < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "not enough points");

        acc.setPoints(after);
        acc.setUpdatedAt(LocalDateTime.now());
        accountRepo.save(acc);

        LoyaltyTransaction tx = new LoyaltyTransaction();
        tx.setUser(entityManager.getReference(User.class, request.getUserId()));
        tx.setType(LoyaltyTransactionType.REDEEM);
        tx.setPoints(-request.getPoints()); // - redeem (حسب تعليقك بالـ entity)
        tx.setNote(request.getNote());
        tx.setCreatedAt(LocalDateTime.now());
        transactionRepo.save(tx);

        return new LoyaltyAccountResponse(acc);
    }

    @Override
    public LoyaltyAccountResponse previewRedeem(RedeemRequest request) {
        if (request == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request is required");
        if (request.getUserId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        if (request.getPoints() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "points must be > 0");

        LoyaltyAccount acc = accountRepo.findByUser_Id(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "loyalty account not found"));

        int after = acc.getPoints() - request.getPoints();
        if (after < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "not enough points");

        // بدون حفظ، وبنفس Response class (بعد ما تضيفي constructor الإضافي)
        return new LoyaltyAccountResponse(acc.getId(), acc.getUser().getId(), after, acc.getUpdatedAt());
    }

    @Override
    public List<LoyaltyTransactionResponse> history(Long userId) {
        if (userId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");

        return transactionRepo.findByUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(LoyaltyTransactionResponse::new)
                .toList();
    }
}
