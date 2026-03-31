package com.project.checkinn.booking.reservation;

import com.project.checkinn.common.CurrencyCode;
import com.project.checkinn.promo.PromoCode;
import com.project.checkinn.user.profile.User;
import com.project.checkinn.catalog.room.Room;

import java.math.BigDecimal;


public class BookingMapper {

    private BookingMapper() {}

    public static BookingResponse toResponse(Booking booking) {
        return booking == null ? null : new BookingResponse(booking);
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
        BookingResponse response = new BookingResponse(booking);
        response.setTotalPrice(displayTotalPrice);
        response.setCurrency(currency);
        response.setOriginalTotalPrice(originalTotalPrice);
        response.setExchangeRate(exchangeRate);
        return response;
    }
}
