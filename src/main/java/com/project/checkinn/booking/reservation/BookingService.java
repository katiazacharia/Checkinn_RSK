package com.project.checkinn.booking.reservation;

import com.project.checkinn.booking.preview.BookingPreviewRequest;
import com.project.checkinn.booking.preview.BookingPreviewResponse;
import com.project.checkinn.common.BookingStatus;
import org.springframework.beans.PropertyValues;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {

    Booking createMyBooking(BookingRequest request, Authentication authentication);

    Booking getById(Long id);

    List<Booking> getAll();

    List<Booking> getByUser(Long userId);

    List<Booking> getMyBookings(Authentication authentication);
    Booking cancel(Long id);
    List<Booking> upcoming(Long userId);
    List<Booking> search(BookingStatus status, Long userId, Long roomId, LocalDate from, LocalDate to);
    BookingPreviewResponse preview(BookingPreviewRequest request);

    List<Booking> getByUserForManager(Long userId, Authentication authentication);
    List<Booking> upcomingForManager(Long userId, Authentication authentication);

}
