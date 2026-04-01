package com.project.checkinn.catalog.room;

import com.project.checkinn.common.RoomStatus;
import com.project.checkinn.common.RoomType;
import java.math.BigDecimal;
import java.util.Set;

public class RoomResponse {

    private Long hotelId;
    private String roomNumber;
    private RoomType type;
    private BigDecimal pricePerNight;
    private String currency;
    private BigDecimal originalPricePerNight;
    private BigDecimal exchangeRate;
    private int capacity;
    private RoomStatus status;
    private String imageUrl;
    private Set<String> amenities;

    public RoomResponse() {
    }

    public RoomResponse(Long hotelId, String roomNumber, RoomType type,
                        BigDecimal pricePerNight, int capacity, RoomStatus status, Set<String> amenities) {

        this.hotelId = hotelId;
        this.roomNumber = roomNumber;
        this.type = type;
        this.pricePerNight = pricePerNight;
        this.capacity = capacity;
        this.status = status;
        this.amenities = amenities;
    }

    public Long getHotelId() { return hotelId; }
    public String getRoomNumber() { return roomNumber; }
    public RoomType getType() { return type; }
    public BigDecimal getPricePerNight() { return pricePerNight; }
    public int getCapacity() { return capacity; }
    public RoomStatus getStatus() { return status; }
    public String getCurrency() { return currency; }
    public BigDecimal getOriginalPricePerNight() { return originalPricePerNight; }
    public BigDecimal getExchangeRate() { return exchangeRate; }
    public String getImageUrl() { return imageUrl; }
    public Set<String> getAmenities() { return amenities; }

    public void setHotelId(Long hotelId) { this.hotelId = hotelId; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public void setType(RoomType type) { this.type = type; }
    public void setPricePerNight(BigDecimal pricePerNight) { this.pricePerNight = pricePerNight; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setStatus(RoomStatus status) { this.status = status; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setOriginalPricePerNight(BigDecimal originalPricePerNight) { this.originalPricePerNight = originalPricePerNight; }
    public void setExchangeRate(BigDecimal exchangeRate) { this.exchangeRate = exchangeRate; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setAmenities(Set<String> amenities) { this.amenities = amenities; }
}