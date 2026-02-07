package com.project.checkinn.booking.reservation;

import com.project.checkinn.common.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BookingResponse {

    private Long id;
    private Long userId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BookingStatus status;
    private BigDecimal totalPrice;
    private Long promoCodeId; // optional

    public BookingResponse(Booking booking) {
        this.id = booking.getId();
        this.userId = booking.getUser() != null ? booking.getUser().getId() : null;
        this.checkInDate = booking.getCheckInDate();
        this.checkOutDate = booking.getCheckOutDate();
        this.status = booking.getStatus();
        this.totalPrice = booking.getTotalPrice();
        this.promoCodeId = booking.getPromoCode() != null ? booking.getPromoCode().getId() : null;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public BookingStatus getStatus() { return status; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public Long getPromoCodeId() { return promoCodeId; }
}
