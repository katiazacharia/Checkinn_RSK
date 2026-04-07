package com.project.checkinn.booking.pricing;

import com.project.checkinn.catalog.room.Room;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PricingServiceImplTest {

    private final PricingServiceImpl service = new PricingServiceImpl();

    @Test
    void calculateTotalPrice_shouldApplyWeekendAndSeasonalMultipliers() {
        Room room = new Room();
        room.setPricePerNight(BigDecimal.valueOf(100));

        BigDecimal total = service.calculateTotalPrice(
                room,
                LocalDate.of(2026, 6, 5),
                LocalDate.of(2026, 6, 7)
        );

        assertNotNull(total);
        assertTrue(total.compareTo(BigDecimal.valueOf(200)) > 0);
    }

    @Test
    void calculateTotalPrice_shouldThrow_whenRoomIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> service.calculateTotalPrice(null, LocalDate.now(), LocalDate.now().plusDays(1)));
    }

    @Test
    void calculateTotalPrice_shouldThrow_whenDatesInvalid() {
        Room room = new Room();
        room.setPricePerNight(BigDecimal.TEN);

        assertThrows(IllegalArgumentException.class,
                () -> service.calculateTotalPrice(room, LocalDate.now(), LocalDate.now()));
    }
}
