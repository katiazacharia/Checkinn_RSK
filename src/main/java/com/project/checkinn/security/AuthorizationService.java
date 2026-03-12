package com.project.checkinn.security;

import com.project.checkinn.booking.reservation.BookingRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component("authz")
public class AuthorizationService {
    private final BookingRepository bookingRepository;

    public AuthorizationService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public boolean isSelfUser(Long userId, Authentication authentication) {
        Long tokenUserId = getUserId(authentication);
        return tokenUserId != null && tokenUserId.equals(userId);
    }

    public boolean isBookingOwner(Long bookingId, Authentication authentication) {
        Long tokenUserId = getUserId(authentication);
        if (tokenUserId == null) return false;

        return bookingRepository.findById(bookingId)
                .map(b -> b.getUser() != null && tokenUserId.equals(b.getUser().getId()))
                .orElse(false);
    }

    private Long getUserId(Authentication authentication) {
        if (authentication == null) return null;
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Jwt jwt)) return null;

        Object claim = jwt.getClaim("userId");
        if (claim == null) return null;

        if (claim instanceof Integer i) return i.longValue();
        if (claim instanceof Long l) return l;
        if (claim instanceof String s) return Long.valueOf(s);

        return null;
    }
}


