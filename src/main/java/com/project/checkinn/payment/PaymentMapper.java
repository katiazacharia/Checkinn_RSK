package com.project.checkinn.payment;

import com.project.checkinn.booking.reservation.Booking;
import com.project.checkinn.common.PaymentMethod;

import java.math.BigDecimal;

public class PaymentMapper {

    private PaymentMapper() {
    }

    public static PaymentResponse toResponse(Payment payment) {
        return payment == null ? null : new PaymentResponse(payment);
    }

    public static PaymentResponse toResponse(
            Payment payment,
            BigDecimal originalAmount,
            BigDecimal loyaltyDiscount,
            Integer redeemedPoints,
            Integer earnedPoints
    ) {
        PaymentResponse response = new PaymentResponse(payment);
        response.setOriginalAmount(originalAmount);
        response.setLoyaltyDiscount(loyaltyDiscount);
        response.setRedeemedPoints(redeemedPoints);
        response.setEarnedPoints(earnedPoints);
        return response;
    }

    public static Payment toEntity(Booking booking, PaymentMethod method) {
        if (booking == null) {
            throw new IllegalArgumentException("booking is required");
        }
        if(method == null){
            throw new IllegalArgumentException("payment method is required");

        }


        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(booking.getTotalPrice());
        payment.setMethod(method);

        return payment;
    }
}