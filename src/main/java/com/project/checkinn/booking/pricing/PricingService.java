package com.project.checkinn.booking.pricing;

import com.project.checkinn.catalog.room.Room;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface PricingService {

    BigDecimal calculateTotalPrice(Room room, LocalDate checkInDate, LocalDate checkOutDate);

}
