package com.project.checkinn.experienceplus;

import com.project.checkinn.common.ExperienceExtraType;
import org.springframework.boot.CommandLineRunner;

import java.math.BigDecimal;
import java.util.List;

public class ExperiencePlusSeeder implements CommandLineRunner {

    private final ExperienceExtraRepo repo;

    public ExperiencePlusSeeder(ExperienceExtraRepo repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) throws Exception {
        if (repo.count() > 0) return;

        repo.saveAll(List.of(
                //Big rewards
                extra("Free Theme Park Ticket", ExperienceExtraType.FREE_THEME_PARK_TICKET,
                        new BigDecimal("150"), 0, 0),

                extra("Room Upgrade", ExperienceExtraType.ROOM_UPGRADE,
                        new BigDecimal("300"), 0, 0),

                extra("Free Spa Access", ExperienceExtraType.FREE_SPA_ACCESS,
                        new BigDecimal("250"), 0, 0),

                //guest reward
                extra("Free 1-Day Car Rental", ExperienceExtraType.FREE_CAR_RENTAL_DAY,
                        BigDecimal.ZERO, 0, 3),

                extra("Free City Shuttle", ExperienceExtraType.FREE_CITY_SHUTTLE,
                        BigDecimal.ZERO, 0, 4),
                //night reward
                extra("Free Breakfast", ExperienceExtraType.FREE_BREAKFAST,
                        BigDecimal.ZERO, 2, 0),

                extra("Free Dinner", ExperienceExtraType.FREE_DINNER,
                        BigDecimal.ZERO, 4, 0),

                extra("Free Gym Access", ExperienceExtraType.FREE_GYM_ACCESS,
                        BigDecimal.ZERO, 3, 0),

                //medium rewards
                extra("Late Checkout", ExperienceExtraType.LATE_CHECKOUT,
                        new BigDecimal("80"), 0, 0),

                extra("Early Check-in", ExperienceExtraType.EARLY_CHECKIN,
                        new BigDecimal("120"), 0, 0),

                //small rewards
                extra("Free Drink", ExperienceExtraType.FREE_DRINK,
                        new BigDecimal("50"), 0, 0)

        ));
    }

    private ExperienceExtra extra(String name,
                                  ExperienceExtraType type,
                                  BigDecimal minAmount,
                                  int minNights,
                                  int minGuests) {

        ExperienceExtra e = new ExperienceExtra();
        e.setName(name);
        e.setType(type);
        e.setMinAmount(minAmount);
        e.setMinNights(minNights);
        e.setMinGuests(minGuests);
        e.setActive(true);

        return e;
    }
}
