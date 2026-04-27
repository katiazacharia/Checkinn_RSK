package com.project.checkinn.payment;

import com.project.checkinn.booking.reservation.Booking;
import com.project.checkinn.common.*;
import com.project.checkinn.exchangerate.ExchangeRateConfig;
import com.project.checkinn.exchangerate.ExchangeRateService;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import com.project.checkinn.loyalty.EarnRequest;
import com.project.checkinn.loyalty.LoyaltyService;
import com.project.checkinn.loyalty.RedeemRequest;
import java.math.RoundingMode;

@Service
  public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepo paymentRepository;
    private final EntityManager entityManager;
    private final NotificationService notificationService;
    private final ExperiencePlusService experiencePlusService;
    private final LoyaltyService loyaltyService;
    private final CurrentUserService currentUserService;
    private final ExchangeRateService exchangeRateService;
    private final ExchangeRateConfig exchangeRateConfig;

    public PaymentServiceImpl(PaymentRepo paymentRepository, EntityManager entityManager,
                              NotificationService notificationService, ExperiencePlusService experiencePlusService, LoyaltyService loyaltyService, CurrentUserService currentUserService, ExchangeRateService exchangeRateService, ExchangeRateConfig exchangeRateConfig) {
        this.paymentRepository = paymentRepository;
        this.entityManager = entityManager;
        this.notificationService = notificationService;
        this.experiencePlusService = experiencePlusService;
        this.loyaltyService = loyaltyService;
        this.currentUserService = currentUserService;
        this.exchangeRateService = exchangeRateService;
        this.exchangeRateConfig = exchangeRateConfig;
    }



    @Override
    public Payment getByBookingId(Long bookingId) {
        return paymentRepository.findByBooking_Id(bookingId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found for this booking"));
    }

@Transactional
    @Override
    public PaymentResponse create(Long bookingId, PaymentMethod method, Integer pointsToRedeem) {
        if (bookingId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bookingId is required");

        if (method == null )
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

    if (booking.getOriginalTotalPrice() == null)
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking original total price is invalid");

    if (pointsToRedeem == null)
        pointsToRedeem = 0;

    if (pointsToRedeem < 0)
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "pointsToRedeem cannot be negative");



        BigDecimal originalAmount = booking.getOriginalTotalPrice();
        BigDecimal finalAmount = originalAmount;
        BigDecimal loyaltyDiscount = BigDecimal.ZERO;
        int redeemedPoints = 0;


        if (pointsToRedeem != null && pointsToRedeem > 0) {
            RedeemRequest redeemRequest = new RedeemRequest();
            redeemRequest.setPoints(pointsToRedeem);
            redeemRequest.setNote("Redeemed in payment for booking #" + booking.getId());
            redeemRequest.setTotalPrice(originalAmount.doubleValue());

            loyaltyService.redeem(booking.getUser().getId(), redeemRequest);

            BigDecimal discountBase = calculateRedeemDiscount(pointsToRedeem); // ILS

            BigDecimal maxDiscount = originalAmount.multiply(BigDecimal.valueOf(0.2));

            if (discountBase.compareTo(maxDiscount) > 0) {
                discountBase = maxDiscount;
            }

            CurrencyCode targetCurrency = booking.getCurrency();
            CurrencyCode baseCurrency = exchangeRateConfig.getBaseCurrency();

            BigDecimal discountConverted;

            if (targetCurrency == baseCurrency) {
                discountConverted = discountBase;
            } else {
                discountConverted = exchangeRateService.convert(discountBase, baseCurrency, targetCurrency);
            }

//  حول السعر قبل الخصم
            BigDecimal amountBeforeDiscount;

            if (targetCurrency == baseCurrency) {
                amountBeforeDiscount = originalAmount;
            } else {
                amountBeforeDiscount = exchangeRateService.convert(originalAmount, baseCurrency, targetCurrency);
            }

//  الخصم يصير على نفس العملة
             finalAmount = amountBeforeDiscount.subtract(discountConverted);

            if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
                finalAmount = BigDecimal.ZERO;
            }

            loyaltyDiscount = discountConverted;
            redeemedPoints = pointsToRedeem;
            if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
                finalAmount = BigDecimal.ZERO;
            }
        }
         BigDecimal convertedAmount = finalAmount.setScale(2, RoundingMode.HALF_UP);

        Payment payment = PaymentMapper.toEntity(booking, method);
        payment.setAmount(convertedAmount);
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());

        int earnedPoints = calculateEarnedPoints(booking);

        payment.setEarnedPoints(earnedPoints);

    booking.setStatus(BookingStatus.CONFIRMED);

    Payment saved = paymentRepository.save(payment);

        if (earnedPoints > 0) {
            EarnRequest earnRequest = new EarnRequest();
            earnRequest.setPoints(earnedPoints);
            earnRequest.setNote("Earned from booking #" + booking.getId());

            loyaltyService.earn(booking.getUser().getId(), earnRequest);
        }

        List<ExperienceExtra> extras = experiencePlusService.assignExtras(booking);
        String message = "Your booking #" + booking.getId() + " has been confirmed.";

        if (!extras.isEmpty()) {
            String extrasText = extras.stream()
                    .map(e -> "- " + e.getName())
                    .reduce("", (a, b) -> a + "\n" + b);

            message += "\n\n ExperiencePlus Rewards:\n" + extrasText;
        }

        notificationService.create(
                booking.getUser().getId(),
                booking.getId(),
                NotificationType.EMAIL,
                "Booking Confirmed",
                message
        );

    String loyaltyMessage;

    CurrencyCode targetCurrency = booking.getCurrency();
    if (redeemedPoints > 0) {
        loyaltyMessage = "Redeemed " + redeemedPoints +
                " points (discount: " + loyaltyDiscount + " " + targetCurrency + ")"
                + " and earned " + earnedPoints + " points";
    } else {
        loyaltyMessage = "You earned " + earnedPoints + " points";
    }

    return new PaymentResponse(
            saved,
            originalAmount,
            redeemedPoints,
            loyaltyDiscount,
            earnedPoints,
            loyaltyMessage,
            targetCurrency
    );
    }

    @Override
    public Payment getById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
    }

    @Override
    public Page<Payment> search(Long bookingId,
                                PaymentStatus status,
                                PaymentMethod method,
                                Pageable pageable,
                                Authentication authentication) {

        Specification<Payment> spec = Specification.where(PaymentSpecification.hasBookingId(bookingId))
                .and(PaymentSpecification.hasStatus(status))
                .and(PaymentSpecification.hasMethod(method));

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean isManager = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"));

        if (isManager && !isAdmin) {
            Long currentUserId = currentUserService.getCurrentUserId(authentication);
            spec = spec.and(PaymentSpecification.hasManagerId(currentUserId));
        }

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


        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Payment already refunded"
            );
        }

        if (payment.getStatus() != PaymentStatus.PAID) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only successful payments can be refunded"
            );
        }
        Integer earnedPoints = payment.getEarnedPoints() != null ? payment.getEarnedPoints() : 0;

        if (earnedPoints > 0) {
            loyaltyService.adjustPoints(
                    payment.getBooking().getUser().getId(),
                    -earnedPoints,
                    "Points reversed for refund on booking #" + payment.getBooking().getId()
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

    private int calculateEarnedPoints(Booking booking) {
        long nights = ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
        return (int) nights * 10;
    }

    private BigDecimal calculateRedeemDiscount(int points) {
        return BigDecimal.valueOf(points)
                .multiply(BigDecimal.valueOf(0.02))
                .setScale(2, RoundingMode.HALF_UP);
    }
}


