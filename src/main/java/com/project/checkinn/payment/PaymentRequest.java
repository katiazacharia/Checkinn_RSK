package com.project.checkinn.payment;

import com.project.checkinn.common.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class PaymentRequest {

    @NotNull(message = "bookingId is required")
    private Long bookingId;

    @NotNull(message = "amount is required")
    @Positive(message = "amount must be > 0")
    private BigDecimal amount;

    @NotNull(message = "payment method is required")
    private PaymentMethod method;

    public PaymentRequest() {}

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }


    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public PaymentMethod getMethod() { return method; }
    public void setMethod(PaymentMethod method) { this.method = method; }
}