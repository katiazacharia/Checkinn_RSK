package com.project.checkinn.loyalty.expiry;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LoyaltyExpiryJob {

    private final LoyaltyExpiryService expiryService;

    public LoyaltyExpiryJob(LoyaltyExpiryService expiryService) {
        this.expiryService = expiryService;
    }

    // بتشتغل كل يوم الساعة 2 الصبح
    @Scheduled(cron = "0 0 2 * * *")
    public void run() {
        expiryService.expireInactiveAccounts();
    }
}