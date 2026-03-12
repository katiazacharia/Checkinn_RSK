package com.project.checkinn.payment;

import com.project.checkinn.booking.reservation.Booking;
import com.project.checkinn.common.BookingStatus;
import com.project.checkinn.common.NotificationType;
import com.project.checkinn.common.PaymentMethod;
import com.project.checkinn.common.PaymentStatus;
import com.project.checkinn.experienceplus.ExperienceExtra;
import com.project.checkinn.experienceplus.ExperiencePlusService;
import com.project.checkinn.notification.NotificationService;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepo paymentRepository;
    private final EntityManager entityManager;
    private final NotificationService notificationService;
    private final ExperiencePlusService experiencePlusService;

    public PaymentServiceImpl(PaymentRepo paymentRepository, EntityManager entityManager,
                              NotificationService notificationService, ExperiencePlusService experiencePlusService) {
        this.paymentRepository = paymentRepository;
        this.entityManager = entityManager;
        this.notificationService = notificationService;
        this.experiencePlusService = experiencePlusService;
    }
    @Transactional
    @Override

    public Payment create(
            Long bookingId,
            PaymentMethod method
    ) {

        if (bookingId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bookingId is required");

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

        if (booking.getTotalPrice() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking total price is invalid");

        booking.setStatus(BookingStatus.CONFIRMED);

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(booking.getTotalPrice());
        payment.setMethod(method);
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());

        Payment saved = paymentRepository.save(payment);

        List<ExperienceExtra> extras = experiencePlusService.assignExtras(booking);
        String message = "Your booking #" + booking.getId() + " has been confirmed.";

        if (!extras.isEmpty()) {

            String extrasText = extras.stream()
                    .map(e -> "- " + e.getName())
                    .reduce("", (a, b) -> a + "\n" + b);

            message += "\n\n🎁 ExperiencePlus Rewards:\n" + extrasText;
        }
        notificationService.create(
                booking.getUser().getId(),
                booking.getId(),
                NotificationType.EMAIL,
                "Booking Confirmed",
                message
        );


        return saved;
    }

    @Override
    public Payment getByBookingId(Long bookingId) {
        return paymentRepository.findByBooking_Id(bookingId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found for this booking"));
    }

    @Override
    public Payment getById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
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


