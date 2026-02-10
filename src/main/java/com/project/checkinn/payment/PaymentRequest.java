package com.project.checkinn.payment;

import com.project.checkinn.common.PaymentMethod;

import java.math.BigDecimal;

public class PaymentRequest {

    private Long bookingId;
    private BigDecimal amount;
    private PaymentMethod method;

    public PaymentRequest() {}
    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public PaymentMethod getMethod() { return method; }
    public void setMethod(PaymentMethod method) { this.method = method; }
}