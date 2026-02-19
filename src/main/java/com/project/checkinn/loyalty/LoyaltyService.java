package com.project.checkinn.loyalty;

import com.project.checkinn.loyalty.account.LoyaltyAccountResponse;
import com.project.checkinn.loyalty.transaction.LoyaltyTransactionResponse;

import java.util.List;

public interface LoyaltyService {
    LoyaltyAccountResponse getAccount(Long userId); //حساب موجود فقط (مش create). إذا مش موجود رجع 404
    LoyaltyAccountResponse earn(EarnRequest request);
    LoyaltyAccountResponse redeem(RedeemRequest request);
    LoyaltyAccountResponse previewRedeem(RedeemRequest request); //بس عشان نعرف شو بصير بلحساب لو عملنا preview بدون ما يحفظ
    List<LoyaltyTransactionResponse> history(Long userId);
}
