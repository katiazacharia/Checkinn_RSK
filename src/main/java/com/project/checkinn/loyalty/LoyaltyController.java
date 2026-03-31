package com.project.checkinn.loyalty;

import com.project.checkinn.loyalty.account.LoyaltyAccountResponse;
import com.project.checkinn.loyalty.transaction.LoyaltyTransactionResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public LoyaltyAccountResponse myAccount(Authentication authentication) {
        return loyaltyService.getMyAccount(authentication);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/user/{userId}")
    public LoyaltyAccountResponse account(@PathVariable Long userId) {
        if (userId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        return loyaltyService.getAccount(userId);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/user/{userId}/balance")
    public LoyaltyAccountResponse balance(@PathVariable Long userId) {
        if (userId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        return loyaltyService.getAccount(userId);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me/balance")
    public LoyaltyAccountResponse myBalance(Authentication authentication) {
        return loyaltyService.getMyAccount(authentication);
    }
    //Earn
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping("/user/{userId}/earn")
    public LoyaltyAccountResponse earnForUser(@PathVariable Long userId,
                                              @Valid @RequestBody EarnRequest request) {
        if (userId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        if (request == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request is required");

        return loyaltyService.earn(userId,request);
    }


    // Redeem + Preview

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/me/redeem")
    public LoyaltyAccountResponse redeem(@Valid @RequestBody RedeemRequest request,
                                         Authentication authentication) {
        if (request == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request is required");
        return loyaltyService.redeemMyPoints(request, authentication);
    }
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping("/user/{userId}/redeem")     // POST /loyalty/user/{userId}/redeem
    public LoyaltyAccountResponse redeemForUser(@PathVariable Long userId,
                                                @Valid @RequestBody RedeemRequest request) {
        if (userId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        if (request == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request is required");

        return loyaltyService.redeem(userId,request);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/me/redeem/preview")
    public LoyaltyAccountResponse previewRedeem(@Valid @RequestBody RedeemRequest request,
                                                Authentication authentication) {
        if (request == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request is required");
        return loyaltyService.previewMyRedeem(request, authentication);
    }

    // POST /loyalty/user/{userId}/redeem/preview
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping("/user/{userId}/redeem/preview")
    public LoyaltyAccountResponse previewRedeemForUser(@PathVariable Long userId,
                                                       @Valid @RequestBody RedeemRequest request) {
        if (userId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        if (request == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request is required");

       return loyaltyService.previewRedeem(userId, request);
    }

    // History
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/user/{userId}/history")
    public List<LoyaltyTransactionResponse> history(@PathVariable Long userId) {
        if (userId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        return loyaltyService.history(userId);
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me/history")
    public List<LoyaltyTransactionResponse> myHistory(Authentication authentication) {
        return loyaltyService.myHistory(authentication);
    }

}
