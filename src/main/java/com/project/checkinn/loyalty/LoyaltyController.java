package com.project.checkinn.loyalty;

import com.project.checkinn.loyalty.account.LoyaltyAccountResponse;
import com.project.checkinn.loyalty.transaction.LoyaltyTransactionResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/loyalty")
public class LoyaltyController {

    private final LoyaltyService loyaltyService;

    public LoyaltyController(LoyaltyService loyaltyService) {
        this.loyaltyService = loyaltyService;
    }

    @GetMapping("/account/{userId}")
    public LoyaltyAccountResponse getOrCreateAccount(@PathVariable Long userId) {
        return loyaltyService.getOrCreate(userId);
    }

    @PostMapping("/earn")
    public LoyaltyAccountResponse earn(@RequestBody EarnRequest request) {
        return loyaltyService.earn(request);
    }

    @PostMapping("/redeem")
    public LoyaltyAccountResponse redeem(@RequestBody RedeemRequest request) {
        return loyaltyService.redeem(request);
    }

    @GetMapping("/history/{userId}")
    public List<LoyaltyTransactionResponse> history(@PathVariable Long userId) {
        return loyaltyService.history(userId);
    }


}
