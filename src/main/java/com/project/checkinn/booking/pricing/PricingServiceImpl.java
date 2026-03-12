package com.project.checkinn.booking.pricing;

import com.project.checkinn.catalog.room.Room;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;

public class PricingServiceImpl implements PricingService{

    private static final BigDecimal WEEKEND_MULTIPLIER = BigDecimal.valueOf(1.20);
    private static final BigDecimal SUMMER_MULTIPLIER = BigDecimal.valueOf(1.30);
    private static final BigDecimal DECEMBER_MULTIPLIER = BigDecimal.valueOf(1.25);


    @Override
    public BigDecimal calculateTotalPrice(Room room, LocalDate checkInDate, LocalDate checkOutDate) {
        if (room == null) {
            throw new IllegalArgumentException("Room is required");
        }
        if (checkInDate == null || checkOutDate == null) {
            throw new IllegalArgumentException("Check-in and check-out dates are required");
        }
        if (!checkOutDate.isAfter(checkInDate)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        BigDecimal total = BigDecimal.ZERO;
        LocalDate currentDate = checkInDate;

        while (currentDate.isBefore(checkOutDate)) {

            BigDecimal dailyPrice = room.getPricePerNight();

            if (isWeekend(currentDate)) {
                dailyPrice = dailyPrice.multiply(WEEKEND_MULTIPLIER);
            }

            dailyPrice = dailyPrice.multiply(getSeasonalMultiplier(currentDate));

            total = total.add(dailyPrice);

            currentDate = currentDate.plusDays(1);
        }

        return total;
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.FRIDAY || day == DayOfWeek.SATURDAY;
    }

    private BigDecimal getSeasonalMultiplier(LocalDate date) {
        int month = date.getMonthValue();

        if (month >= 6 && month <= 8) {
            return SUMMER_MULTIPLIER;
        }

        if (month == 12) {
            return DECEMBER_MULTIPLIER;
        }
        return BigDecimal.ONE;
    }
    }
