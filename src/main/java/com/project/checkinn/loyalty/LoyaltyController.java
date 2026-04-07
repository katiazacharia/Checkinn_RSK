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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{userId}")
    public LoyaltyAccountResponse account(@PathVariable Long userId) {
        if (userId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        return loyaltyService.getAccount(userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
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



    // History
    @PreAuthorize("hasRole('ADMIN')")
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
