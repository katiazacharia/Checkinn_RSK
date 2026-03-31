package com.project.checkinn.payment;

import com.project.checkinn.booking.reservation.Booking;
import com.project.checkinn.common.BookingStatus;
import com.project.checkinn.common.NotificationType;
import com.project.checkinn.common.PaymentMethod;
import com.project.checkinn.common.PaymentStatus;
import com.project.checkinn.experienceplus.ExperienceExtra;
import com.project.checkinn.experienceplus.ExperiencePlusService;
import com.project.checkinn.notification.NotificationService;
import com.project.checkinn.security.CurrentUserService;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
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
    private final CurrentUserService currentUserService;

    public PaymentServiceImpl(PaymentRepo paymentRepository, EntityManager entityManager,
                              NotificationService notificationService, ExperiencePlusService experiencePlusService, CurrentUserService currentUserService) {
        this.paymentRepository = paymentRepository;
        this.entityManager = entityManager;
        this.notificationService = notificationService;
        this.experiencePlusService = experiencePlusService;
        this.currentUserService = currentUserService;
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

        Payment payment = PaymentMapper.toEntity(booking, method);
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
        Specification<Payment> spec = Specification.where(PaymentSpecification.hasBookingId(bookingId))
                .and(PaymentSpecification.hasStatus(status))
                .and(PaymentSpecification.hasMethod(method));

        return paymentRepository.findAll(spec, pageable);
    }

    @Override
    public Payment getMyPaymentById(Long id, Authentication authentication) {
        Long currentUserId = currentUserService.getCurrentUserId(authentication);

        Specification<Payment> spec = Specification.where(PaymentSpecification.hasPaymentId(id))
                .and(PaymentSpecification.hasUserId(currentUserId));

        return paymentRepository.findOne(spec)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
    }

    @Override
    public Payment getMyPaymentByBookingId(Long bookingId, Authentication authentication) {
        Long currentUserId = currentUserService.getCurrentUserId(authentication);

        Specification<Payment> spec = Specification.where(PaymentSpecification.hasBookingId(bookingId))
                .and(PaymentSpecification.hasUserId(currentUserId));

        return paymentRepository.findOne(spec)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found for this booking"));
    }

    @Override
    public Page<Payment> searchMy(Long bookingId, PaymentStatus status, PaymentMethod method,
                                  Pageable pageable, Authentication authentication) {
        Long currentUserId = currentUserService.getCurrentUserId(authentication);

        Specification<Payment> spec = Specification.where(PaymentSpecification.hasUserId(currentUserId))
                .and(PaymentSpecification.hasBookingId(bookingId))
                .and(PaymentSpecification.hasStatus(status))
                .and(PaymentSpecification.hasMethod(method));

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

    @Override
    public Payment refund(Long bookingId) {

        Payment payment = paymentRepository.findByBooking_Id(bookingId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));

        if (payment.getStatus() != PaymentStatus.PAID) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only successful payments can be refunded"
            );
        }

        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Payment already refunded"
            );
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        Booking booking = payment.getBooking();
        if (booking != null && booking.getStatus() != BookingStatus.CANCELLED) {
            booking.setStatus(BookingStatus.CANCELLED);
        }

        return paymentRepository.save(payment);
    }

    @Override
    public Payment refundMy(Long bookingId, Authentication authentication) {
        Long currentUserId = currentUserService.getCurrentUserId(authentication);

        Payment payment = getMyPaymentByBookingId(bookingId, authentication);

        if (!payment.getBooking().getUser().getId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }


        return refund(bookingId);
    }
}


