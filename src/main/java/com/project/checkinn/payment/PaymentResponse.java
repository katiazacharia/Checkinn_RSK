package com.project.checkinn.payment;

import com.project.checkinn.common.PaymentMethod;
import com.project.checkinn.common.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentResponse {

    private Long id;
    private Long bookingId;
    private BigDecimal amount;
    private PaymentMethod method;
    private PaymentStatus status;
    private LocalDateTime paidAt;
    private BigDecimal originalAmount;
    private Integer redeemedPoints;
    private BigDecimal loyaltyDiscount;
    private Integer earnedPoints;
    private String loyaltyMessage;

    public PaymentResponse() {}


    public PaymentResponse(Payment payment, BigDecimal originalAmount, Integer redeemedPoints, BigDecimal loyaltyDiscount, Integer earnedPoints, String loyaltyMessage) {
        this.id = payment.getId();
        this.bookingId = payment.getBooking().getId();
        this.amount = payment.getAmount();
        this.method = payment.getMethod();
        this.status = payment.getStatus();
        this.paidAt = payment.getPaidAt();
        this.originalAmount = originalAmount;
        this.redeemedPoints = redeemedPoints;
        this.loyaltyDiscount = loyaltyDiscount;
        this.earnedPoints = earnedPoints;
        this.loyaltyMessage = loyaltyMessage;
    }


    public PaymentResponse(Payment payment) {
        this.id = payment.getId();
        this.bookingId = payment.getBooking().getId();
        this.amount = payment.getAmount();
        this.method = payment.getMethod();
        this.status = payment.getStatus();
        this.paidAt = payment.getPaidAt();
    }

    public Long getId() { return id; }
    public Long getBookingId() { return bookingId; }
    public BigDecimal getAmount() { return amount; }
    public PaymentMethod getMethod() { return method; }
    public PaymentStatus getStatus() { return status; }
    public LocalDateTime getPaidAt() { return paidAt; }

    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }

    public Integer getRedeemedPoints() {
        return redeemedPoints;
    }

    public BigDecimal getLoyaltyDiscount() {
        return loyaltyDiscount;
    }

    public Integer getEarnedPoints() {
        return earnedPoints;
    }

    public String getLoyaltyMessage() {
        return loyaltyMessage;
    }

    public void setLoyaltyMessage(String loyaltyMessage) {
        this.loyaltyMessage = loyaltyMessage;
    }

    public void setEarnedPoints(Integer earnedPoints) {
        this.earnedPoints = earnedPoints;
    }

    public void setLoyaltyDiscount(BigDecimal loyaltyDiscount) {
        this.loyaltyDiscount = loyaltyDiscount;
    }

    public void setRedeemedPoints(Integer redeemedPoints) {
        this.redeemedPoints = redeemedPoints;
    }

    public void setOriginalAmount(BigDecimal originalAmount) {
        this.originalAmount = originalAmount;
    }
}