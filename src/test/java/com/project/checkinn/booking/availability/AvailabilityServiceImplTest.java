package com.project.checkinn.booking.availability;

import com.project.checkinn.booking.reservation.BookingRepository;
import com.project.checkinn.common.BookingStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceImplTest {

    @Mock
    BookingRepository bookingRepository;

    @InjectMocks
    AvailabilityServiceImpl service;

    private AvailabilityRequest request;

    @BeforeEach
    void setUp() {
        request = new AvailabilityRequest();
        request.setRoomId(1L);
        request.setCheckInDate(LocalDate.of(2026, 5, 10));
        request.setCheckOutDate(LocalDate.of(2026, 5, 12));
    }

    @Test
    void check_shouldReturnAvailableTrue_whenNoConflicts() {
        when(bookingRepository.countByRoom_IdAndStatusNotAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                1L, BookingStatus.CANCELLED, request.getCheckOutDate(), request.getCheckInDate()
        )).thenReturn(0L);

        AvailabilityResponse response = service.check(request);

        assertTrue(response.isAvailable());
        assertEquals(request.getCheckInDate(), response.getCheckInDate());
        assertEquals(request.getCheckOutDate(), response.getCheckOutDate());
    }

    @Test
    void check_shouldReturnAvailableFalse_whenConflictsExist() {
        when(bookingRepository.countByRoom_IdAndStatusNotAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                1L, BookingStatus.CANCELLED, request.getCheckOutDate(), request.getCheckInDate()
        )).thenReturn(2L);

        AvailabilityResponse response = service.check(request);

        assertFalse(response.isAvailable());
    }

    @Test
    void check_shouldThrowBadRequest_whenRequestIsNull() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.check(null));
        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    void check_shouldThrowBadRequest_whenCheckoutBeforeCheckin() {
        request.setCheckOutDate(request.getCheckInDate());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.check(request));
        assertTrue(ex.getReason().contains("checkOutDate must be after checkInDate"));
    }
}
