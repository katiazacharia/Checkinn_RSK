package com.project.checkinn.user.profile;


import com.project.checkinn.security.AppUser;
import com.project.checkinn.security.AppUserRepository;
import com.project.checkinn.security.Role;
import com.project.checkinn.loyalty.account.LoyaltyAccount;
import com.project.checkinn.loyalty.account.LoyaltyAccountRepo;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final LoyaltyAccountRepo loyaltyAccountRepo;
    private final AppUserRepository appuserRepo;

    public UserServiceImpl(UserRepo userRepo, LoyaltyAccountRepo loyaltyAccountRepo, AppUserRepository appuserRepo) {
        this.userRepo = userRepo;
        this.loyaltyAccountRepo = loyaltyAccountRepo;
        this.appuserRepo = appuserRepo;
    }

    @Override
    public UserResponse create(UserCreateRequest request,Authentication authentication) {

        if (request == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request is required");

        Long appUserId = getUserId(authentication);

        if (appUserId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        User user = userRepo.findById(appUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User profile not found"));

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            boolean emailTaken = userRepo.existsByEmail(request.getEmail()) &&
                    (user.getEmail() == null || !user.getEmail().equals(request.getEmail()));

            if (emailTaken) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
            }

            user.setEmail(request.getEmail());
        }
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        User savedUser = userRepo.save(user);

        if (!loyaltyAccountRepo.existsByUser_Id(savedUser.getId())) {
            LoyaltyAccount acc = new LoyaltyAccount();
            acc.setUser(savedUser);
            acc.setPoints(0);
            acc.setUpdatedAt(LocalDateTime.now());
            loyaltyAccountRepo.save(acc);
        }

        return new UserResponse(savedUser);
    }
    @Override
    public List<UserResponse> getAll() {
        return userRepo.findAll()
                .stream()
                .map(UserResponse::new)
                .toList();
    }
    @Override
    public UserResponse getById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return new UserResponse(user);
    }

    private Long getUserId(Authentication authentication) {
        if (authentication == null) return null;

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Jwt jwt)) return null;

        Object claim = jwt.getClaim("userId");
        if (claim == null) return null;

        if (claim instanceof Integer i) return i.longValue();
        if (claim instanceof Long l) return l;

        if (claim instanceof String s) {
            try {
                return Long.valueOf(s);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return null;
    }
}
