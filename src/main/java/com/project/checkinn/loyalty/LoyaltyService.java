package com.project.checkinn.loyalty;

import com.project.checkinn.common.LoyaltyTransactionType;
import com.project.checkinn.loyalty.account.LoyaltyAccountResponse;
import com.project.checkinn.loyalty.transaction.LoyaltyTransactionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface LoyaltyService {
    LoyaltyAccountResponse getAccount(Long userId); //حساب موجود فقط (مش create). إذا مش موجود رجع 404
    LoyaltyAccountResponse earn(EarnRequest request);
    LoyaltyAccountResponse redeem(RedeemRequest request);
    LoyaltyAccountResponse previewRedeem(RedeemRequest request); //بس عشان نعرف شو بصير بلحساب لو عملنا preview بدون ما يحفظ
    List<LoyaltyTransactionResponse> history(Long userId); //ما يلمس القديم

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
