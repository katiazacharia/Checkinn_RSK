package com.project.checkinn.catalog.room;

import com.project.checkinn.common.RoomStatus;
import com.project.checkinn.common.RoomType;

import java.math.BigDecimal;

public class RoomRequest {

    private Long hotelId;
    private String roomNumber;
    private RoomType type;
    private BigDecimal pricePerNight;
    private int capacity;
    private RoomStatus status;

    public RoomRequest() {}

    public Long getHotelId() { return hotelId; }
    public void setHotelId(Long hotelId) { this.hotelId = hotelId; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public RoomType getType() { return type; }
    public void setType(RoomType type) { this.type = type; }

    public BigDecimal getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(BigDecimal pricePerNight) { this.pricePerNight = pricePerNight; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public RoomStatus getStatus() { return status; }
    public void setStatus(RoomStatus status) { this.status = status; }
}