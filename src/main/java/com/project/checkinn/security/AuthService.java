package com.project.checkinn.security;



import com.project.checkinn.user.profile.User;
import com.project.checkinn.user.profile.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AuthService {

    private final AppUserRepository userRepo;
    private final PasswordEncoder encoder;
    private final JwtTokenService tokenService;
    private final RefreshTokenRepository refreshTokenRepo;
    private final long refreshTokenDays;
    private final UserRepo profileRepo;

    public AuthService(
            AppUserRepository userRepo,
            PasswordEncoder encoder,
            JwtTokenService tokenService,
            RefreshTokenRepository refreshTokenRepo,
            @Value("${security.jwt.secret.refresh-token-days}") long refreshTokenDays, UserRepo profileRepo
    ) {
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.tokenService = tokenService;
        this.refreshTokenRepo = refreshTokenRepo;
        this.refreshTokenDays = refreshTokenDays;
        this.profileRepo = profileRepo;
    }

    @Transactional
    public RegisterResponse register(RegisterRequest req) {
        if (userRepo.existsByUsername(req.username())) {
            throw new RuntimeException("Username already exists");
        }
        if (profileRepo.existsByEmail(req.email())) {
            throw new RuntimeException("Email already exists");
        }
        AppUser appuser = new AppUser();
        appuser.setUsername(req.username());
        appuser.setPasswordHash(encoder.encode(req.password()));
        appuser.setEnabled(true);
        appuser.setRole(Role.CUSTOMER);

        AppUser savedAppUser = userRepo.save(appuser);

        User profile = new User();
        profile.setAppUser(savedAppUser);
        profile.setFullName(req.fullName());
        profile.setEmail(req.email());
        profile.setPhone(req.phone());
        profile.setRole(Role.CUSTOMER);

        profileRepo.save(profile);

        return new RegisterResponse(
                savedAppUser.getId(),
                savedAppUser.getUsername(),
                savedAppUser.getRole().name()
        );
    }

    @Transactional
    public LoginResponse login(String username, String password) {

        AppUser user = userRepo.findByUsername(username)
                .filter(AppUser::isEnabled)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!encoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        User profile = profileRepo.findByAppUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("User profile not found"));

        List<String> roles = List.of(user.getRole().name());

        String accessToken = tokenService.generateAccessToken(
                user.getUsername(),
                roles,
                user.getId()
        );

        String refreshToken = createRefreshToken(user);

        return new LoginResponse(
                accessToken,
                refreshToken,
                "Bearer",
                tokenService.getAccessTokenExpiresInSeconds()
        );
    }

    @Transactional
    public LoginResponse refreshToken(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepo.findByToken(refreshTokenValue)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (refreshToken.isRevoked()) {
            throw new RuntimeException("Refresh token has been revoked");
        }

        if (refreshToken.isExpired()) {
            refreshTokenRepo.delete(refreshToken);
            throw new RuntimeException("Refresh token has expired");
        }

        AppUser user = refreshToken.getUser();

        if (!user.isEnabled()) {
            throw new RuntimeException("User account is disabled");
        }

        List<String> roles = List.of(user.getRole().name());

        String accessToken = tokenService.generateAccessToken(
                user.getUsername(),
                roles,
                user.getId()
        );

        String newRefreshToken = rotateRefreshToken(refreshToken);

        return new LoginResponse(
                accessToken,
                newRefreshToken,
                "Bearer",
                tokenService.getAccessTokenExpiresInSeconds()
        );
    }

    private String createRefreshToken(AppUser user) {
        String token = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plusSeconds(refreshTokenDays * 24 * 60 * 60);

        RefreshToken refreshToken = new RefreshToken(token, user, expiryDate);
        refreshTokenRepo.save(refreshToken);

        return token;
    }

    private String rotateRefreshToken(RefreshToken oldToken) {
        refreshTokenRepo.delete(oldToken);
        return createRefreshToken(oldToken.getUser());
    }

    @Transactional
    public void revokeRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepo.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        refreshToken.setRevoked(true);
        refreshTokenRepo.save(refreshToken);
    }
}