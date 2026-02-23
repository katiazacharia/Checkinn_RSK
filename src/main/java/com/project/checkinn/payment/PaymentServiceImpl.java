package com.project.checkinn.payment;

import com.project.checkinn.booking.reservation.Booking;
import com.project.checkinn.common.BookingStatus;
import com.project.checkinn.common.NotificationType;
import com.project.checkinn.common.PaymentMethod;
import com.project.checkinn.common.PaymentStatus;
import com.project.checkinn.notification.NotificationService;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepo paymentRepository;
    private final EntityManager entityManager;
    private final NotificationService notificationService;

    public PaymentServiceImpl(PaymentRepo paymentRepository, EntityManager entityManager,
                              NotificationService notificationService) {
        this.paymentRepository = paymentRepository;
        this.entityManager = entityManager;
        this.notificationService = notificationService;
    }
    @Transactional
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

        if (amount.signum() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "amount must be > 0");


        if (method == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "payment method is required");

        if (paymentRepository.existsByBooking_Id(bookingId))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Payment already exists for this booking");
        Booking booking = entityManager.find(Booking.class, bookingId);

        if (booking == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found");

        if (booking.getStatus() == BookingStatus.CANCELLED)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot pay for a cancelled booking");

        if (booking.getStatus() == BookingStatus.CONFIRMED)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Booking already confirmed");

        booking.setStatus(BookingStatus.CONFIRMED);

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(amount);
        payment.setMethod(method);
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());

        Payment saved = paymentRepository.save(payment);

        notificationService.create(
                booking.getUser().getId(),
                booking.getId(),
                NotificationType.EMAIL,
                "Booking Confirmed",
                "Your booking #" + booking.getId() + " has been confirmed."
        );


        return saved;
    }

    @Override
    public Payment getById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
    }


    @Override
    public Payment getByBookingId(Long bookingId) {
        return paymentRepository.findByBooking_Id(bookingId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found for this booking"));
    }

    @Override
    public Page<Payment> search(Long bookingId, PaymentStatus status, PaymentMethod method, Pageable pageable) {

        Specification<Payment> spec = (root, query, cb) -> cb.conjunction();

        if (bookingId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("booking").get("id"), bookingId));
        }
        if (status != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("status"),status));
        }
        if (method != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("method"),method));
        }

        return paymentRepository.findAll(spec, pageable);
    }

    @Transactional
    @Override
    public Payment updateStatus(Long id, PaymentStatus status) {

        if (status == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "status is required");

        Payment payment = getById(id);
        payment.setStatus(status);

        if (status == PaymentStatus.PAID && payment.getPaidAt() == null) {
            payment.setPaidAt(LocalDateTime.now());
        }

        return paymentRepository.save(payment);
    }
}


