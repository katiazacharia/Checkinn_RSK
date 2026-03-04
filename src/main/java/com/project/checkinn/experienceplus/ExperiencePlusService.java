package com.project.checkinn.experienceplus;

import com.project.checkinn.booking.reservation.Booking;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ExperiencePlusService {

    private final ExperienceExtraRepo repo;

    public ExperiencePlusService(ExperienceExtraRepo repo) {
        this.repo = repo;
    }
    public List<ExperienceExtra> assignExtras(Booking booking) {
        if (booking.getExtras() != null && !booking.getExtras().isEmpty()) {
            return List.of();
        }
        int nights = (int) ChronoUnit.DAYS.between(
                booking.getCheckInDate(),
                booking.getCheckOutDate()
        );
        BigDecimal amount = booking.getTotalPrice() == null ? BigDecimal.ZERO : booking.getTotalPrice();

        int guests = booking.getGuests();
        List<ExperienceExtra> activeExtras = repo.findByActiveTrue();


        List<ExperienceExtra> eligible = activeExtras.stream()
                .filter(e -> matches(e, amount, nights, guests))
                .limit(3) // optional: max 3 extras per booking
                .toList();

        if (!eligible.isEmpty()) {
            Set<ExperienceExtra> set = eligible.stream().collect(Collectors.toSet());
            booking.setExtras(set);
        }

        return eligible;
    }

    private boolean matches(ExperienceExtra e, BigDecimal amount, int nights, int guests) {

        BigDecimal minAmount = e.getMinAmount() == null ? BigDecimal.ZERO : e.getMinAmount();

        boolean okAmount = amount.compareTo(minAmount) >= 0;
        boolean okNights = nights >= e.getMinNights();
        boolean okGuests = guests >= e.getMinGuests();

        return okAmount && okNights && okGuests;
    }
    }
