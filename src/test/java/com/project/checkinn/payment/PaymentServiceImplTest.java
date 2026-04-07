package com.project.checkinn.payment;

import com.project.checkinn.booking.reservation.Booking;
import com.project.checkinn.common.*;
import com.project.checkinn.exchangerate.ExchangeRateConfig;
import com.project.checkinn.exchangerate.ExchangeRateService;
import com.project.checkinn.experienceplus.ExperiencePlusService;
import com.project.checkinn.loyalty.EarnRequest;
import com.project.checkinn.loyalty.LoyaltyService;
import com.project.checkinn.notification.NotificationService;
import com.project.checkinn.security.CurrentUserService;
import com.project.checkinn.user.profile.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock PaymentRepo paymentRepository;
    @Mock EntityManager entityManager;
    @Mock NotificationService notificationService;
    @Mock ExperiencePlusService experiencePlusService;
    @Mock LoyaltyService loyaltyService;
    @Mock CurrentUserService currentUserService;
    @Mock ExchangeRateService exchangeRateService;
    @Mock ExchangeRateConfig exchangeRateConfig;
    @Mock Authentication authentication;

    @InjectMocks PaymentServiceImpl service;

    private Booking booking;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        booking = new Booking();
        booking.setUser(user);
        booking.setId(10L);
        booking.setStatus(BookingStatus.PENDING);
        booking.setOriginalTotalPrice(BigDecimal.valueOf(200));
        booking.setTotalPrice(BigDecimal.valueOf(200));
        booking.setCurrency(CurrencyCode.ILS);
        booking.setCheckInDate(LocalDate.of(2026, 5, 1));
        booking.setCheckOutDate(LocalDate.of(2026, 5, 3));
        booking.setGuests(2);
    }



    @Test
    void create_shouldThrowConflict_whenPaymentAlreadyExists() {
        when(paymentRepository.existsByBooking_Id(10L)).thenReturn(true);
        assertThrows(ResponseStatusException.class, () -> service.create(10L, PaymentMethod.CARD, 0));
    }

    @Test
    void refund_shouldReverseEarnedPoints_andSetRefunded() {
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setStatus(PaymentStatus.PAID);
        payment.setEarnedPoints(20);
        when(paymentRepository.findByBooking_Id(10L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        Payment saved = service.refund(10L);

        assertEquals(PaymentStatus.REFUNDED, saved.getStatus());
        verify(loyaltyService).adjustPoints(eq(user.getId()), eq(-20), contains("refund"));
        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
    }

    @Test
    void getById_shouldThrow_whenMissing() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> service.getById(99L));
    }
}
