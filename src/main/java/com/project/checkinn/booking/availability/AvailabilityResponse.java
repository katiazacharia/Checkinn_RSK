package com.project.checkinn.booking.availability;

import java.time.LocalDate;

public class AvailabilityResponse {

    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private boolean available;


    public AvailabilityResponse(LocalDate checkInDate, LocalDate checkOutDate, boolean available) {
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.available = available;

    }
    public LocalDate getCheckInDate() { return checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public boolean isAvailable() { return available; }

}
