package com.project.checkinn.security;

import com.project.checkinn.user.profile.User;
import com.project.checkinn.user.profile.UserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CurrentUserService {
    private final UserRepo userRepo;

    public CurrentUserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }


    public Long getCurrentUserId(Authentication authentication) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthenticated");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Jwt jwt)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        Object claim = jwt.getClaim("userId");
        if (claim == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing userId claim");
        }
        if (claim instanceof Integer i) return i.longValue();
        if (claim instanceof Long l) return l;

        if (claim instanceof String s) {
            try {
                return Long.valueOf(s);
            } catch (NumberFormatException e) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid userId");
            }
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid userId type");
    }

    public User getCurrentUser(Authentication authentication) {
        Long userId = getCurrentUserId(authentication);

        return userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    }
