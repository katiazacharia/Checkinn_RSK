package com.project.checkinn.payment;

import com.project.checkinn.booking.reservation.Booking;

public class PaymentMapper {

    private PaymentMapper() {
    }

    // Entity -> Response
    public static PaymentResponse toResponse(Payment payment) {
        if (payment == null) {
            return null;
        }
        return new PaymentResponse(payment);
    }

    // Request -> Entity
    public static Payment toEntity(PaymentRequest request, Booking booking) {
        if (request == null || booking == null) {
            return null;
        }

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(request.getAmount());
        payment.setMethod(request.getMethod());

        return payment;
    }
}