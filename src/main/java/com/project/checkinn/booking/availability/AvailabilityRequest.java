package com.project.checkinn.booking.availability;

import java.time.LocalDate;

public class AvailabilityRequest {
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Long roomId;


    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }

    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }

    public Long getRoomId() {
        return roomId; }
    public void setRoomId(Long roomId) {
        this.roomId = roomId; }
}
