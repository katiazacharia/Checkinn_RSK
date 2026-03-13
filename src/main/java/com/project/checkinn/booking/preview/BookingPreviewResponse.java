package com.project.checkinn.booking.preview;

import java.math.BigDecimal;

public class BookingPreviewResponse {

    private boolean available;
    private boolean capacityOk;
    private BigDecimal totalPrice;
    private String message;
    public BookingPreviewResponse() {
    }

    public BookingPreviewResponse(boolean available, boolean capacityOk, BigDecimal totalPrice, String message) {
        this.available = available;
        this.capacityOk = capacityOk;
        this.totalPrice = totalPrice;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
