package com.project.checkinn.booking.reservation;

import com.project.checkinn.promo.PromoCode;
import com.project.checkinn.user.profile.User;

public class BookingMapper {

    private BookingMapper() {}

    public static BookingResponse toResponse(Booking booking) {
        return booking == null ? null : new BookingResponse(booking);
    }

    public static Booking toEntity(BookingRequest request, User user, PromoCode promoCode) {
        if (request == null || user == null) return null;

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setCheckInDate(request.getCheckInDate());
        booking.setCheckOutDate(request.getCheckOutDate());
        booking.setTotalPrice(request.getTotalPrice());
        booking.setPromoCode(promoCode);
        return booking;
    }
}
