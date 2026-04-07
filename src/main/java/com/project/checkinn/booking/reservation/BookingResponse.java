package com.project.checkinn.booking.reservation;

import com.project.checkinn.common.BookingStatus;
import com.project.checkinn.common.CurrencyCode;
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
    private BigDecimal originalTotalPrice;
    private BigDecimal exchangeRate;
    private CurrencyCode currency;
    private BigDecimal totalPrice;
    private Long promoCodeId; // optional
    private Long roomId;
    private List<String> extras;
    private Integer expectedLoyaltyPoints;
    private String loyaltyMessage;


    public BookingResponse(Booking booking, Integer expectedLoyaltyPoints, String loyaltyMessage) {
        this.id = booking.getId();
        this.userId = booking.getUser() != null ? booking.getUser().getId() : null;
        this.checkInDate = booking.getCheckInDate();
        this.checkOutDate = booking.getCheckOutDate();
        this.status = booking.getStatus();
        this.totalPrice = booking.getTotalPrice();
        this.currency = booking.getCurrency();
        this.originalTotalPrice = booking.getOriginalTotalPrice();
        this.exchangeRate = booking.getExchangeRate();
        this.promoCodeId = booking.getPromoCode() != null ? booking.getPromoCode().getId() : null;
        this.roomId = booking.getRoom() != null ? booking.getRoom().getId() : null;
        this.expectedLoyaltyPoints = expectedLoyaltyPoints;
        this.loyaltyMessage = loyaltyMessage;
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

    public Integer getExpectedLoyaltyPoints() {
        return expectedLoyaltyPoints;
    }

    public void setExpectedLoyaltyPoints(Integer expectedLoyaltyPoints) {
        this.expectedLoyaltyPoints = expectedLoyaltyPoints;
    }

    public String getLoyaltyMessage() {
        return loyaltyMessage;
    }

    public void setLoyaltyMessage(String loyaltyMessage) {
        this.loyaltyMessage = loyaltyMessage;
    }

    public CurrencyCode getCurrency() {
        return currency;
    }

    public BigDecimal getOriginalTotalPrice() {
        return originalTotalPrice;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public void setOriginalTotalPrice(BigDecimal originalTotalPrice) {
        this.originalTotalPrice = originalTotalPrice;
    }

    public void setCurrency(CurrencyCode currency) {
        this.currency = currency;
    }

    public Long getRoomId() { return roomId; }
    public List<String> getExtras() {
        return extras;
    }
}
