package com.project.checkinn.loyalty;

import com.project.checkinn.loyalty.account.LoyaltyAccountResponse;
import com.project.checkinn.loyalty.transaction.LoyaltyTransactionResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@RestController
@RequestMapping("/loyalty")
public class LoyaltyController {

    private final LoyaltyService loyaltyService;

    public LoyaltyController(LoyaltyService loyaltyService) {

        this.loyaltyService = loyaltyService;
    }

    // Account / Balance

    @GetMapping("/user/{userId}")
    public LoyaltyAccountResponse account(@PathVariable Long userId) {
        if (userId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        return loyaltyService.getAccount(userId);
    }

    // نرجع نفس LoyaltyAccountResponse عشان ما نعمل كلاس جديد
    @GetMapping("/user/{userId}/balance")
    public LoyaltyAccountResponse balance(@PathVariable Long userId) {
        if (userId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        return loyaltyService.getAccount(userId);
    }

    //Earn

    @PostMapping("/earn")
    public LoyaltyAccountResponse earn(@Valid @RequestBody EarnRequest request) {
        if (request == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request is required");
        return loyaltyService.earn(request);
    }

    @PostMapping("/user/{userId}/earn")
    public LoyaltyAccountResponse earnForUser(@PathVariable Long userId,
                                              @Valid @RequestBody EarnRequest request) {
        if (userId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        if (request == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request is required");

        request.setUserId(userId);
        return loyaltyService.earn(request);
    }


    // Redeem + Preview

    @PostMapping("/redeem")  // POST /loyalty/redeem
    public LoyaltyAccountResponse redeem(@Valid @RequestBody RedeemRequest request) {
        if (request == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request is required");
        return loyaltyService.redeem(request);
    }

    @PostMapping("/user/{userId}/redeem")     // POST /loyalty/user/{userId}/redeem

    public LoyaltyAccountResponse redeemForUser(@PathVariable Long userId,
                                                @Valid @RequestBody RedeemRequest request) {
        if (userId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        if (request == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request is required");

        request.setUserId(userId);
        return loyaltyService.redeem(request);
    }

    @PostMapping("/redeem/preview")  // نفس RedeemRequest ونفس LoyaltyAccountResponse بس بدون حفظ
    public LoyaltyAccountResponse previewRedeem(@Valid @RequestBody RedeemRequest request) {
        if (request == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request is required");
        return loyaltyService.previewRedeem(request);
    }

    // POST /loyalty/user/{userId}/redeem/preview
    @PostMapping("/user/{userId}/redeem/preview")
    public LoyaltyAccountResponse previewRedeemForUser(@PathVariable Long userId,
                                                       @Valid @RequestBody RedeemRequest request) {
        if (userId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        if (request == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request is required");

        request.setUserId(userId);
        return loyaltyService.previewRedeem(request);
    }

    // History

    @GetMapping("/user/{userId}/history")
    public List<LoyaltyTransactionResponse> history(@PathVariable Long userId) {
        if (userId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        return loyaltyService.history(userId);
    }

}
