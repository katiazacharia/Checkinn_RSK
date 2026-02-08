package com.project.checkinn.catalog.room;

import com.project.checkinn.common.RoomStatus;
import com.project.checkinn.common.RoomType;
import java.math.BigDecimal;

public class RoomResponse {
    private Long id;
    private Long hotelId;
    private String roomNumber;
    private RoomType type;
    private BigDecimal pricePerNight;
    private int capacity;
    private RoomStatus status;

    public RoomResponse() {}

    public RoomResponse(Long id, Long hotelId, String roomNumber, RoomType type,
                        BigDecimal pricePerNight, int capacity, RoomStatus status) {
        this.id = id;
        this.hotelId = hotelId;
        this.roomNumber = roomNumber;
        this.type = type;
        this.pricePerNight = pricePerNight;
        this.capacity = capacity;
        this.status = status;
    }

    public Long getId() { return id; }
    public Long getHotelId() { return hotelId; }
    public String getRoomNumber() { return roomNumber; }
    public RoomType getType() { return type; }
    public BigDecimal getPricePerNight() { return pricePerNight; }
    public int getCapacity() { return capacity; }
    public RoomStatus getStatus() { return status; }

    public void setId(Long id) { this.id = id; }
    public void setHotelId(Long hotelId) { this.hotelId = hotelId; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public void setType(RoomType type) { this.type = type; }
    public void setPricePerNight(BigDecimal pricePerNight) { this.pricePerNight = pricePerNight; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setStatus(RoomStatus status) { this.status = status; }
}