package com.project.checkinn.booking.reservation;

import java.util.List;

public interface BookingService {

    Booking create(BookingRequest request);

    Booking getById(Long id);

    List<Booking> getAll();

    List<Booking> getByUser(Long userId);

    Booking cancel(Long id);
}
