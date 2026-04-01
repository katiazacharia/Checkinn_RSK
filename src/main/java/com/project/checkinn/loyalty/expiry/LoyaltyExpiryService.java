package com.project.checkinn.loyalty.expiry;

import com.project.checkinn.common.LoyaltyTransactionType;
import com.project.checkinn.loyalty.account.LoyaltyAccount;
import com.project.checkinn.loyalty.account.LoyaltyAccountRepo;
import com.project.checkinn.loyalty.transaction.LoyaltyTransaction;
import com.project.checkinn.loyalty.transaction.LoyaltyTransactionRepo;
import com.project.checkinn.common.Tier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
@Service
public class LoyaltyExpiryService {

    private final LoyaltyAccountRepo accountRepo;
    private final LoyaltyTransactionRepo transactionRepo;

    public LoyaltyExpiryService(LoyaltyAccountRepo accountRepo,
                                LoyaltyTransactionRepo transactionRepo) {
        this.accountRepo = accountRepo;
        this.transactionRepo = transactionRepo;
    }

    @Transactional
    public void expireInactiveAccounts() {
        // كل حساب ما تحدث من 12 شهر
        LocalDateTime cutoff = LocalDateTime.now().minusMonths(12);

        List<LoyaltyAccount> expired = accountRepo.findByUpdatedAtBeforeAndPointsGreaterThan(cutoff, 0);

        for (LoyaltyAccount acc : expired) {

            LoyaltyTransaction tx = new LoyaltyTransaction();
            tx.setUser(acc.getUser());
            tx.setType(LoyaltyTransactionType.EXPIRE);
            tx.setPoints(-acc.getPoints());
            tx.setNote("Points expired due to 12 months inactivity");
            tx.setCreatedAt(LocalDateTime.now());
            transactionRepo.save(tx);

            // صفّر النقاط ورجّع التير لـ BRONZE
            acc.setPoints(0);
            acc.recalculateTier();
            acc.setUpdatedAt(LocalDateTime.now());
            accountRepo.save(acc);
        }
    }
        }


