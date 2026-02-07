package com.project.checkinn.payment;

import com.project.checkinn.booking.reservation.Booking;
import com.project.checkinn.common.PaymentMethod;
import com.project.checkinn.common.PaymentStatus;
import jakarta.persistence.EntityManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepo paymentRepository;
    private final EntityManager entityManager;

    public PaymentServiceImpl(PaymentRepo paymentRepository,
                              EntityManager entityManager) {
        this.paymentRepository = paymentRepository;
        this.entityManager = entityManager;
    }

    @Override
    public Payment create(
            Long bookingId,
            BigDecimal amount,
            PaymentMethod method
    ) {

        if (bookingId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bookingId is required");

        if (amount == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "amount is required");

        if (method == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "payment method is required");

        Booking bookingRef = entityManager.getReference(Booking.class, bookingId);

        Payment payment = new Payment();
        payment.setBooking(bookingRef);
        payment.setAmount(amount);
        payment.setMethod(method);
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());

        return paymentRepository.save(payment);
    }

    @Override
    public Payment getById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
    }

    @Override
    public List<Payment> getAll() {
        return paymentRepository.findAll();
    }
}