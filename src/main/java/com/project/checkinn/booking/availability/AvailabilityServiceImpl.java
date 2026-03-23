package com.project.checkinn.booking.availability;

import com.project.checkinn.booking.reservation.BookingRepository;
import com.project.checkinn.common.BookingStatus;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
public class AvailabilityServiceImpl  implements AvailabilityService{

    private final BookingRepository bookingRepository;

    public AvailabilityServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public AvailabilityResponse check(AvailabilityRequest request) {
        if(request == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request is required");

        if (request.getRoomId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "roomId is required");

        LocalDate in = request.getCheckInDate();
        LocalDate out = request.getCheckOutDate();

        if (in == null || out == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "checkInDate and checkOutDate are required");

        if (!out.isAfter(in))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "checkOutDate must be after checkInDate");
        long conflicts = bookingRepository
                .countByRoom_IdAndStatusNotAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                        request.getRoomId(),
                        BookingStatus.CANCELLED,
                        out,
                        in
                );

        boolean available = conflicts==0;
        return new AvailabilityResponse(in, out, available, conflicts);

    }
}
