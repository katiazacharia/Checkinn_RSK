package com.project.checkinn.booking.reservation;

import com.project.checkinn.common.CurrencyCode;
import com.project.checkinn.promo.PromoCode;
import com.project.checkinn.user.profile.User;
import com.project.checkinn.catalog.room.Room;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class BookingMapper {

    private BookingMapper() {}

    public static BookingResponse toResponse(Booking booking) {
        if (booking == null) return null;

        int expectedLoyaltyPoints = booking.getTotalPrice() != null
                ? booking.getOriginalTotalPrice().divide(BigDecimal.TEN, 0, RoundingMode.DOWN).intValue()
                : 0;

        String loyaltyMessage = "If you complete payment, you will earn " + expectedLoyaltyPoints + " loyalty points.";

        return new BookingResponse(booking, expectedLoyaltyPoints, loyaltyMessage);
    }

    public static Booking toEntity(BookingRequest request, User user, Room room, PromoCode promoCode) {
        if (request == null || user == null || room == null) return null;

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckInDate(request.getCheckInDate());
        booking.setCheckOutDate(request.getCheckOutDate());
        booking.setPromoCode(promoCode);
        booking.setGuests(request.getGuests());
        return booking;
    }

    public static BookingResponse toResponse(Booking booking, BigDecimal displayTotalPrice,
                                             CurrencyCode currency,
                                             BigDecimal originalTotalPrice,
                                             BigDecimal exchangeRate){

        if (booking == null) return null;

        int expectedLoyaltyPoints = displayTotalPrice != null
                ? displayTotalPrice.divide(BigDecimal.TEN, 0, RoundingMode.DOWN).intValue()
                : 0;

        String loyaltyMessage = "If you complete payment, you will earn " + expectedLoyaltyPoints + " loyalty points.";

        BookingResponse response = new BookingResponse(booking, expectedLoyaltyPoints, loyaltyMessage);
        response.setTotalPrice(displayTotalPrice);
        response.setCurrency(currency);
        response.setOriginalTotalPrice(originalTotalPrice);
        response.setExchangeRate(exchangeRate);
        return response;
    }
}
