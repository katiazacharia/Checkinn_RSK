package com.project.checkinn.security;

import com.project.checkinn.loyalty.account.LoyaltyAccountRepo;
import com.project.checkinn.user.profile.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock AppUserRepository userRepo;
    @Mock PasswordEncoder encoder;
    @Mock JwtTokenService tokenService;
    @Mock RefreshTokenRepository refreshTokenRepo;
    @Mock UserRepo profileRepo;
    @Mock LoyaltyAccountRepo loyaltyAccountRepo;

    AuthService service;

    // ✅ الحل النهائي: إنشاء service يدويًا
    @BeforeEach
    void setUp() {
        service = new AuthService(
                userRepo,
                encoder,
                tokenService,
                refreshTokenRepo,
                7L,
                profileRepo,
                loyaltyAccountRepo
        );
    }

    // ========================= TESTS =========================

    @Test
    void register_shouldThrowConflict_whenUsernameExists() {
        RegisterRequest req = new RegisterRequest(
                "shahd",
                "Pass123!",
                "Shahd",
                "s@test.com",
                "0591234567",
                "CUSTOMER"
        );

        when(userRepo.existsByUsername("shahd")).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> service.register(req));
    }

    @Test
    void login_shouldThrowUnauthorized_whenPasswordWrong() {
        AppUser appUser = new AppUser();
        appUser.setUsername("shahd");
        appUser.setPasswordHash("hashed");
        appUser.setEnabled(true);
        appUser.setRole(Role.CUSTOMER);

        when(userRepo.findByUsername("shahd")).thenReturn(Optional.of(appUser));
        when(encoder.matches("wrong", "hashed")).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> service.login("shahd", "wrong"));
    }
}