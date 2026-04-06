package com.project.checkinn.security;



import com.project.checkinn.user.profile.User;
import com.project.checkinn.user.profile.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        if (profileRepo.existsByEmail(req.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");        }
        AppUser appuser = new AppUser();
        appuser.setUsername(req.username());
        appuser.setPasswordHash(encoder.encode(req.password()));
        appuser.setEnabled(true);
        Role selectedRole = parseRegisterRole(req.role());
        appuser.setRole(selectedRole);


        AppUser savedAppUser = userRepo.save(appuser);

        User profile = new User();
        profile.setAppUser(savedAppUser);
        profile.setFullName(req.fullName());
        profile.setEmail(req.email());
        profile.setPhone(req.phone());
        profile.setRole(selectedRole);

        profileRepo.save(profile);

        return new RegisterResponse(
                savedAppUser.getId(),
                savedAppUser.getUsername(),
                savedAppUser.getRole().name()
        );
    }

    private Role parseRegisterRole(String roleValue) {
        if (roleValue == null || roleValue.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Role is required. Allowed values: CUSTOMER or MANAGER"
            );
        }

        String normalized = roleValue.trim().toUpperCase();

        if (normalized.equals("ADMIN")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "You cannot register as ADMIN"
            );
        }

        if (!normalized.equals("CUSTOMER") && !normalized.equals("MANAGER")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid role. Allowed values: CUSTOMER or MANAGER"
            );
        }

        return Role.valueOf(normalized);
    }

    @Transactional
    public LoginResponse login(String username, String password) {

        AppUser user = userRepo.findByUsername(username)
                .filter(AppUser::isEnabled)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!encoder.matches(password, user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        User profile = profileRepo.findByAppUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User profile not found"));

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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

        if (refreshToken.isRevoked()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token has been revoked");
        }

        if (refreshToken.isExpired()) {
            refreshTokenRepo.delete(refreshToken);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token has expired");
        }

        AppUser user = refreshToken.getUser();

        if (!user.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User account is disabled");
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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Refresh token not found"));

        refreshToken.setRevoked(true);
        refreshTokenRepo.save(refreshToken);
    }
}
