package com.project.checkinn.security;

import com.project.checkinn.booking.reservation.BookingRepository;
import com.project.checkinn.review.ReviewRepo;
import com.project.checkinn.user.favorite.FavoriteRepo;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component("authz")
public class AuthorizationService {
    private final CurrentUserService currentUserService;
    private final BookingRepository bookingRepository;
    private final ReviewRepo reviewRepo;
    private final FavoriteRepo favoriteRepo;

    public AuthorizationService(CurrentUserService currentUserService,
                                BookingRepository bookingRepository,
                                ReviewRepo reviewRepo,
                                FavoriteRepo favoriteRepo) {
        this.currentUserService = currentUserService;
        this.bookingRepository = bookingRepository;
        this.reviewRepo = reviewRepo;
        this.favoriteRepo = favoriteRepo;
    }

    public boolean isSelfUser(Long userId, Authentication authentication) {
        Long tokenUserId = currentUserService.getCurrentUserId(authentication);
        return tokenUserId != null && tokenUserId.equals(userId);
    }

    public boolean isBookingOwner(Long bookingId, Authentication authentication) {
        Long tokenUserId = currentUserService.getCurrentUserId(authentication);
        if (tokenUserId == null) return false;

        return bookingRepository.findById(bookingId)
                .map(b -> b.getUser() != null
                        && tokenUserId.equals(b.getUser().getId()))
                .orElse(false);
    }

    public boolean isReviewOwner(Long reviewId, Authentication authentication) {

        if (reviewId == null) return false;

        Long currentUserId = currentUserService.getCurrentUserId(authentication);

        return reviewRepo.findById(reviewId)
                .map(review -> review.getUser().getId().equals(currentUserId))
                .orElse(false);
    }

    public boolean isFavoriteOwner(Long favoriteId, Authentication authentication) {
        if (favoriteId == null) return false;

        Long currentUserId = currentUserService.getCurrentUserId(authentication);
        if (currentUserId == null) return false;

        return favoriteRepo.findById(favoriteId)
                .map(fav -> fav.getUser().getId().equals(currentUserId))
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


