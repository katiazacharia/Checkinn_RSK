package com.project.checkinn.booking.reservation;

import com.project.checkinn.common.BookingStatus;
import com.project.checkinn.experienceplus.ExperienceExtra;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class BookingResponse {

    private Long id;
    private Long userId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BookingStatus status;
    private BigDecimal totalPrice;
    private Long promoCodeId; // optional
    private Long roomId;
    private List<String> extras;


    public BookingResponse(Booking booking) {
        this.id = booking.getId();
        this.userId = booking.getUser() != null ? booking.getUser().getId() : null;
        this.checkInDate = booking.getCheckInDate();
        this.checkOutDate = booking.getCheckOutDate();
        this.status = booking.getStatus();
        this.totalPrice = booking.getTotalPrice();
        this.promoCodeId = booking.getPromoCode() != null ? booking.getPromoCode().getId() : null;
        this.roomId = booking.getRoom() != null ? booking.getRoom().getId() : null;
        if (booking.getExtras() != null) {
            this.extras = booking.getExtras()
                    .stream()
                    .map(ExperienceExtra::getName)
                    .collect(Collectors.toList());
        }
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public BookingStatus getStatus() { return status; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public Long getPromoCodeId() { return promoCodeId; }
    public Long getRoomId() { return roomId; }
    public List<String> getExtras() {
        return extras;
    }
}
