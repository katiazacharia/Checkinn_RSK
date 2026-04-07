package com.project.checkinn.loyalty;

import com.project.checkinn.common.LoyaltyTransactionType;
import com.project.checkinn.loyalty.account.LoyaltyAccount;
import com.project.checkinn.loyalty.account.LoyaltyAccountRepo;
import com.project.checkinn.loyalty.transaction.LoyaltyTransaction;
import com.project.checkinn.loyalty.transaction.LoyaltyTransactionRepo;
import com.project.checkinn.security.CurrentUserService;
import com.project.checkinn.user.profile.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoyaltyServiceImplTest {

    @Mock LoyaltyAccountRepo accountRepo;
    @Mock LoyaltyTransactionRepo transactionRepo;
    @Mock EntityManager entityManager;
    @Mock CurrentUserService currentUserService;
    @Mock Authentication authentication;

    @InjectMocks LoyaltyServiceImpl service;

    private LoyaltyAccount account;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        account = new LoyaltyAccount();
        account.setUser(user);
        account.setPoints(100);
        account.setUpdatedAt(LocalDateTime.now());
        account.recalculateTier();
    }

    @Test
    void getAccount_shouldThrow_whenNotFound() {
        when(accountRepo.findByUser_Id(1L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> service.getAccount(1L));
    }

    @Test
    void earn_shouldIncreasePoints_andCreateTransaction() {
        EarnRequest req = new EarnRequest();
        req.setPoints(50);
        req.setNote("test");
        when(accountRepo.findByUser_Id(1L)).thenReturn(Optional.of(account));
        when(entityManager.getReference(eq(User.class), eq(1L))).thenReturn(user);

        var response = service.earn(1L, req);

        assertEquals(150, response.getPoints());
        verify(transactionRepo).save(any(LoyaltyTransaction.class));
    }

    @Test
    void redeem_shouldThrow_whenInsufficientPoints() {
        RedeemRequest req = new RedeemRequest();
        req.setPoints(200);
        req.setTotalPrice(500);
        when(accountRepo.findByUser_Id(1L)).thenReturn(Optional.of(account));

        assertThrows(ResponseStatusException.class, () -> service.redeem(1L, req));
    }

    @Test
    void previewMyRedeem_shouldUseCurrentUser() {
        RedeemRequest req = new RedeemRequest();
        req.setPoints(20);
        when(currentUserService.getCurrentUserId(authentication)).thenReturn(1L);
        when(accountRepo.findByUser_Id(1L)).thenReturn(Optional.of(account));

        var response = service.previewMyRedeem(req, authentication);

        assertEquals(80, response.getPoints());
    }
}
