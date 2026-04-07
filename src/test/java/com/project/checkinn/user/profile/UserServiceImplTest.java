package com.project.checkinn.user.profile;

import com.project.checkinn.loyalty.account.LoyaltyAccountRepo;
import com.project.checkinn.security.CurrentUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock UserRepo userRepo;
    @Mock LoyaltyAccountRepo loyaltyAccountRepo;
    @Mock CurrentUserService currentUserService;
    @Mock Authentication authentication;

    @InjectMocks UserServiceImpl service;



    @Test
    void getMyProfile_shouldThrow_whenMissingUser() {
        when(currentUserService.getCurrentUserId(authentication)).thenReturn(1L);
        when(userRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> service.getMyProfile(authentication));
    }
}
