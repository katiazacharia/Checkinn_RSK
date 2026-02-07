package com.project.checkinn.loyalty;

import com.project.checkinn.loyalty.dto.EarnRequest;
import com.project.checkinn.loyalty.dto.RedeemRequest;
import com.project.checkinn.loyalty.account.LoyaltyAccountResponse;
import com.project.checkinn.loyalty.transaction.LoyaltyTransactionResponse;

import java.util.List;

public interface LoyaltyService {
    LoyaltyAccountResponse getOrCreate(Long userId);
    LoyaltyAccountResponse earn(EarnRequest request);
    LoyaltyAccountResponse redeem(RedeemRequest request);
    List<LoyaltyTransactionResponse> history(Long userId);
}