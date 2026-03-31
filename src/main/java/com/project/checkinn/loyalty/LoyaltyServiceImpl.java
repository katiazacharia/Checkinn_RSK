package com.project.checkinn.loyalty;

import com.project.checkinn.common.LoyaltyTransactionType;
import com.project.checkinn.loyalty.account.LoyaltyAccount;
import com.project.checkinn.loyalty.account.LoyaltyAccountRepo;
import com.project.checkinn.loyalty.account.LoyaltyAccountResponse;
import com.project.checkinn.loyalty.transaction.LoyaltyTransaction;
import com.project.checkinn.loyalty.transaction.LoyaltyTransactionRepo;
import com.project.checkinn.loyalty.transaction.LoyaltyTransactionResponse;
import com.project.checkinn.security.CurrentUserService;
import com.project.checkinn.user.profile.User;

import jakarta.persistence.EntityManager;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import com.project.checkinn.loyalty.transaction.LoyaltyTransactionSpecs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;


@Service
public class LoyaltyServiceImpl implements LoyaltyService {

    private final LoyaltyAccountRepo accountRepo;
    private final LoyaltyTransactionRepo transactionRepo;
    private final EntityManager entityManager;
    private final CurrentUserService currentUserService;

    public LoyaltyServiceImpl(LoyaltyAccountRepo accountRepo,
                              LoyaltyTransactionRepo transactionRepo,
                              EntityManager entityManager, CurrentUserService currentUserService) {
        this.accountRepo = accountRepo;
        this.transactionRepo = transactionRepo;
        this.entityManager = entityManager;
        this.currentUserService = currentUserService;
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
    public LoyaltyAccountResponse getMyAccount(Authentication authentication) {
        Long currentUserId = currentUserService.getCurrentUserId(authentication);
        return getAccount(currentUserId);
    }

    @Override
    public LoyaltyAccountResponse earn(Long userId,EarnRequest request) {
        if (request == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request is required");
        if (userId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        if (request.getPoints() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "points must be > 0");
        LoyaltyAccount acc = accountRepo.findByUser_Id(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "loyalty account not found"));

        acc.setPoints(acc.getPoints() + request.getPoints());
        acc.setUpdatedAt(LocalDateTime.now());
        accountRepo.save(acc);

        LoyaltyTransaction tx = new LoyaltyTransaction();
        tx.setUser(entityManager.getReference(User.class, userId));
        tx.setType(LoyaltyTransactionType.EARN);
        tx.setPoints(request.getPoints()); // + earn
        tx.setNote(request.getNote());
        tx.setCreatedAt(LocalDateTime.now());
        transactionRepo.save(tx);

        return new LoyaltyAccountResponse(acc);
    }

    @Override
    public LoyaltyAccountResponse redeem(Long userId,RedeemRequest request) {
        if (request == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request is required");
        if (userId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        if (request.getPoints() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "points must be > 0");

        LoyaltyAccount acc = accountRepo.findByUser_Id(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "loyalty account not found"));

        int after = acc.getPoints() - request.getPoints();
        if (after < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "not enough points");

        acc.setPoints(after);
        acc.setUpdatedAt(LocalDateTime.now());
        accountRepo.save(acc);

        LoyaltyTransaction tx = new LoyaltyTransaction();
        tx.setUser(entityManager.getReference(User.class,userId));
        tx.setType(LoyaltyTransactionType.REDEEM);
        tx.setPoints(-request.getPoints()); // - redeem (حسب تعليقك بالـ entity)
        tx.setNote(request.getNote());
        tx.setCreatedAt(LocalDateTime.now());
        transactionRepo.save(tx);

        return new LoyaltyAccountResponse(acc);
    }

    @Override
    public LoyaltyAccountResponse redeemMyPoints(RedeemRequest request, Authentication authentication) {
        Long currentUserId = currentUserService.getCurrentUserId(authentication);
        return redeem(currentUserId,request);
    }

    @Override
    public LoyaltyAccountResponse previewRedeem(Long userId, RedeemRequest request) {
        if (request == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request is required");
        if (userId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        if (request.getPoints() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "points must be > 0");

        LoyaltyAccount acc = accountRepo.findByUser_Id(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "loyalty account not found"));

        int after = acc.getPoints() - request.getPoints();
        if (after < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "not enough points");

        // بدون حفظ، وبنفس Response class (بعد ما تضيفي constructor الإضافي)
        return new LoyaltyAccountResponse(acc.getId(), acc.getUser().getId(), after, acc.getUpdatedAt());
    }

    @Override
    public LoyaltyAccountResponse previewMyRedeem(RedeemRequest request, Authentication authentication) {
        Long currentUserId = currentUserService.getCurrentUserId(authentication);

        return previewRedeem(currentUserId, request);
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

    @Override
    public List<LoyaltyTransactionResponse> myHistory(Authentication authentication) {
        Long currentUserId = currentUserService.getCurrentUserId(authentication);
        return history(currentUserId);
    }

    @Override
    public Page<LoyaltyTransactionResponse> historyPaged(
            Long userId,
            LoyaltyTransactionType type,
            LocalDateTime from,
            LocalDateTime to,
            Integer minPoints,
            Integer maxPoints,
            String noteQ,
            Pageable pageable
    ) {
        if (userId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");

        Specification<LoyaltyTransaction> spec =
                Specification.where(LoyaltyTransactionSpecs.byUserId(userId))
                        .and(LoyaltyTransactionSpecs.type(type))
                        .and(LoyaltyTransactionSpecs.createdFrom(from))
                        .and(LoyaltyTransactionSpecs.createdTo(to))
                        .and(LoyaltyTransactionSpecs.pointsBetween(minPoints, maxPoints))
                        .and(LoyaltyTransactionSpecs.noteContains(noteQ));

        return transactionRepo.findAll(spec, pageable)
                .map(LoyaltyTransactionResponse::new);
    }

}
