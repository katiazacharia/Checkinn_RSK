package com.project.checkinn.booking.reservation;

import com.project.checkinn.booking.preview.BookingPreviewRequest;
import com.project.checkinn.booking.preview.BookingPreviewResponse;
import com.project.checkinn.common.BookingStatus;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {

    Booking create(BookingRequest request);

    Booking getById(Long id);

    List<Booking> getAll();

    List<Booking> getByUser(Long userId);

    Booking cancel(Long id);
    List<Booking> upcoming(Long userId);
    List<Booking> search(BookingStatus status, Long userId, Long roomId, LocalDate from, LocalDate to);
    BookingPreviewResponse preview(BookingPreviewRequest request);
}
