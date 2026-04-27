package com.project.checkinn.security;

import com.project.checkinn.booking.reservation.BookingRepository;
import com.project.checkinn.catalog.hotel.HotelRepo;
import com.project.checkinn.catalog.room.RoomRepo;
import com.project.checkinn.payment.PaymentRepo;
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
    private final HotelRepo hotelRepository;
    private final RoomRepo roomRepo;
    private final PaymentRepo paymentRepo;


    public AuthorizationService(CurrentUserService currentUserService,
                                BookingRepository bookingRepository,
                                ReviewRepo reviewRepo,
                                FavoriteRepo favoriteRepo, HotelRepo hotelRepository, RoomRepo roomRepo, PaymentRepo paymentRepo) {
        this.currentUserService = currentUserService;
        this.bookingRepository = bookingRepository;
        this.reviewRepo = reviewRepo;
        this.favoriteRepo = favoriteRepo;
        this.hotelRepository = hotelRepository;
        this.roomRepo = roomRepo;
        this.paymentRepo = paymentRepo;
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
                .map(review -> review.getUser() != null && review.getUser().getId().equals(currentUserId))
                .orElse(false);
    }

    public boolean isFavoriteOwner(Long favoriteId, Authentication authentication) {
        if (favoriteId == null) return false;

        Long currentUserId = currentUserService.getCurrentUserId(authentication);
        if (currentUserId == null) return false;

        return favoriteRepo.findById(favoriteId)
                .map(fav -> fav.getUser() != null && fav.getUser().getId().equals(currentUserId))
                .orElse(false);
    }




    public boolean isHotelManager(Long hotelId, Authentication authentication) {
        if (hotelId == null) return false;

        Long currentUserId = currentUserService.getCurrentUserId(authentication);
        if (currentUserId == null) return false;

        return hotelRepository.findById(hotelId)
                .map(hotel -> hotel.getManager() != null
                        && hotel.getManager().getId().equals(currentUserId))
                .orElse(false);
    }

    public boolean canManageRoom(Long roomId, Authentication authentication) {
        if (roomId == null) return false;

        Long currentUserId = currentUserService.getCurrentUserId(authentication);

        return roomRepo.findById(roomId)
                .map(room -> room.getHotel() != null
                        && room.getHotel().getManager() != null
                        && room.getHotel().getManager().getId().equals(currentUserId))
                .orElse(false);
    }

    public boolean canManagerAccessBooking(Long bookingId, Authentication authentication) {
        if (bookingId == null) return false;

        Long currentUserId = currentUserService.getCurrentUserId(authentication);
        if (currentUserId == null) return false;

        return bookingRepository.findById(bookingId)
                .map(b -> b.getRoom() != null
                        && b.getRoom().getHotel() != null
                        && b.getRoom().getHotel().getManager() != null
                        && b.getRoom().getHotel().getManager().getId().equals(currentUserId))
                .orElse(false);
    }

    public boolean canManagerAccessPayment(Long paymentId, Authentication authentication) {
        if (paymentId == null) return false;

        Long currentUserId = currentUserService.getCurrentUserId(authentication);
        if (currentUserId == null) return false;

        return paymentRepo.findById(paymentId)
                .map(payment -> payment.getBooking() != null
                        && payment.getBooking().getRoom() != null
                        && payment.getBooking().getRoom().getHotel() != null
                        && payment.getBooking().getRoom().getHotel().getManager() != null
                        && payment.getBooking().getRoom().getHotel().getManager().getId().equals(currentUserId))
                .orElse(false);
    }

    public boolean canManagerAccessReview(Long reviewId, Authentication authentication) {
        if (reviewId == null) return false;

        Long currentUserId = currentUserService.getCurrentUserId(authentication);
        if (currentUserId == null) return false;

        return reviewRepo.findById(reviewId)
                .map(review -> review.getBooking() != null
                        && review.getBooking().getRoom() != null
                        && review.getBooking().getRoom().getHotel() != null
                        && review.getBooking().getRoom().getHotel().getManager() != null
                        && review.getBooking().getRoom().getHotel().getManager().getId().equals(currentUserId))
                .orElse(false);
    }
}


