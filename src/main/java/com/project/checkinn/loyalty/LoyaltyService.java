package com.project.checkinn.loyalty;

import com.project.checkinn.common.LoyaltyTransactionType;
import com.project.checkinn.loyalty.account.LoyaltyAccountResponse;
import com.project.checkinn.loyalty.transaction.LoyaltyTransactionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.List;

public interface LoyaltyService {
    //الي فيهم my هدول لليوزر هو شو بقدر يعمل و يسوي و يشوف
    LoyaltyAccountResponse getAccount(Long userId); //حساب موجود فقط (مش create). إذا مش موجود رجع 404
    LoyaltyAccountResponse getMyAccount(Authentication authentication);

    LoyaltyAccountResponse earn(Long userId,EarnRequest request);

    LoyaltyAccountResponse redeem(Long userId,RedeemRequest request);
    LoyaltyAccountResponse redeemMyPoints(RedeemRequest request, Authentication authentication);

    LoyaltyAccountResponse previewRedeem(Long userId,RedeemRequest request); //بس عشان نعرف شو بصير بلحساب لو عملنا preview بدون ما يحفظ
    LoyaltyAccountResponse previewMyRedeem(RedeemRequest request, Authentication authentication);


    List<LoyaltyTransactionResponse> history(Long userId); //ما يلمس القديم
    List<LoyaltyTransactionResponse> myHistory(Authentication authentication);


    Page<LoyaltyTransactionResponse> historyPaged(
            Long userId,
            LoyaltyTransactionType type,
            LocalDateTime from,
            LocalDateTime to,
            Integer minPoints,
            Integer maxPoints,
            String noteQ,
            Pageable pageable
    );
}
