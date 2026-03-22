package com.project.checkinn.booking.preview;

import java.math.BigDecimal;

public class BookingPreviewResponse {

    private boolean available;
    private boolean capacityOk;
    private BigDecimal totalPrice;
    private String currency;
    private BigDecimal originalTotalPrice;
    private BigDecimal exchangeRate;
    private String message;
    public BookingPreviewResponse() {
    }

    public BookingPreviewResponse(boolean available, boolean capacityOk, BigDecimal totalPrice, String currency, BigDecimal originalTotalPrice, BigDecimal exchangeRate, String message) {
        this.available = available;
        this.capacityOk = capacityOk;
        this.totalPrice = totalPrice;
        this.currency = currency;
        this.originalTotalPrice = originalTotalPrice;
        this.exchangeRate = exchangeRate;
        this.message = message;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isCapacityOk() {
        return capacityOk;
    }

    public void setCapacityOk(boolean capacityOk) {
        this.capacityOk = capacityOk;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getOriginalTotalPrice() {
        return originalTotalPrice;
    }

    public void setOriginalTotalPrice(BigDecimal originalTotalPrice) {
        this.originalTotalPrice = originalTotalPrice;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
