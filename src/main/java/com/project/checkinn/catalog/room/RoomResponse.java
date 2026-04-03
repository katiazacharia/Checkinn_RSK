package com.project.checkinn.catalog.room;

import com.project.checkinn.common.RoomStatus;
import com.project.checkinn.common.RoomType;
import java.math.BigDecimal;
import java.util.Set;

public class RoomResponse {

    private Long id;
    private Long hotelId;
    private RoomType type;
    private String roomNumber;
    private BigDecimal pricePerNight;
    private String currency;
    private BigDecimal originalPricePerNight;
    private BigDecimal exchangeRate;
    private int capacity;
    private RoomStatus status;
    private  Set<String> imageUrls;

    public RoomResponse() {
    }

    public RoomResponse(Long id, Long hotelId, RoomType type, String roomNumber,
                        BigDecimal pricePerNight, int capacity, RoomStatus status, Set<String> imageUrls) {

        this.id = id;
        this.hotelId = hotelId;
        this.type = type;
        this.roomNumber = roomNumber;
        this.pricePerNight = pricePerNight;
        this.capacity = capacity;
        this.status = status;
        this.imageUrls = imageUrls;
    }

    public Long getId() { return id; }
    public Long getHotelId() { return hotelId; }
    public RoomType getType() { return type; }
    public BigDecimal getPricePerNight() { return pricePerNight; }
    public int getCapacity() { return capacity; }
    public RoomStatus getStatus() { return status; }
    public String getCurrency() { return currency; }
    public BigDecimal getOriginalPricePerNight() { return originalPricePerNight; }
    public BigDecimal getExchangeRate() { return exchangeRate; }
    public Set<String> getImageUrls() { return imageUrls; }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public void setId(Long id) { this.id = id; }
    public void setHotelId(Long hotelId) { this.hotelId = hotelId; }
    public void setType(RoomType type) { this.type = type; }
    public void setPricePerNight(BigDecimal pricePerNight) { this.pricePerNight = pricePerNight; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setStatus(RoomStatus status) { this.status = status; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setOriginalPricePerNight(BigDecimal originalPricePerNight) { this.originalPricePerNight = originalPricePerNight; }
    public void setExchangeRate(BigDecimal exchangeRate) { this.exchangeRate = exchangeRate; }
    public void setImageUrls(Set<String> imageUrls) { this.imageUrls = imageUrls; }
}