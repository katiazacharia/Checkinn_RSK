package com.project.checkinn.payment;

import com.project.checkinn.booking.reservation.Booking;

public class PaymentMapper {

    private PaymentMapper() {
    }

    public static PaymentResponse toResponse(Payment payment) {
        return payment == null ? null : new PaymentResponse(payment);
    }

    public static Payment toEntity(PaymentRequest request, Booking booking) {
        if (request == null || booking == null) {
            return null;
        }

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(booking.getTotalPrice());
        payment.setMethod(request.getMethod());

        return payment;
    }
}