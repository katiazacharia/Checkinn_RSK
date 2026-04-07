package com.project.checkinn.payment;

import com.project.checkinn.common.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public class PaymentRequest {

    @NotNull(message = "bookingId is required")
    private Long bookingId;

    @NotNull(message = "payment method is required")
    private String method;

    public PaymentRequest() {}

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
}