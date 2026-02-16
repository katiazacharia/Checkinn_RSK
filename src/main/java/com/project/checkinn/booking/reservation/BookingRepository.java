package com.project.checkinn.booking.reservation;

import com.project.checkinn.common.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUser_Id(Long userId);

    long countByRoom_IdAndStatusNotAndCheckInDateLessThanAndCheckOutDateGreaterThan(
            Long roomId,
            BookingStatus status,
            LocalDate outDate,
            LocalDate inDate
    );

}
