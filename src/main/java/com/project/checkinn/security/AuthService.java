package com.project.checkinn.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    private final AppUserRepository userRepo;
    private final PasswordEncoder encoder;
    private final JwtTokenService tokenService;

    public AuthService(AppUserRepository userRepo, PasswordEncoder encoder, JwtTokenService tokenService) {
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.tokenService = tokenService;
    }

    public LoginResponse login(LoginRequest req) {

        AppUser user = userRepo.findByUsername(req.username())
                .filter(AppUser::isEnabled)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!encoder.matches(req.password(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        List<String> roles = List.of(user.getRole().name());

        String token = tokenService.generateAccessToken(
                user.getUsername(),
                roles,
                user.getId()
        );
        return new LoginResponse(token, "Bearer", tokenService.getAccessTokenExpiresInSeconds());
    }
}
