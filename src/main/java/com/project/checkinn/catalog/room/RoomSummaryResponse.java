package com.project.checkinn.catalog.room;

import com.project.checkinn.common.RoomType;

import java.math.BigDecimal;

public class RoomSummaryResponse {

    private Long id;
    private RoomType type;
    private BigDecimal pricePerNight;
    private int capacity;

    public RoomSummaryResponse() {
    }

    public RoomSummaryResponse(Long id, RoomType type, BigDecimal pricePerNight, int capacity) {
        this.id = id;
        this.type = type;
        this.pricePerNight = pricePerNight;
        this.capacity = capacity;
    }

    public Long getId() { return id; }
    public RoomType getType() { return type; }
    public BigDecimal getPricePerNight() { return pricePerNight; }
    public int getCapacity() { return capacity; }

    public void setId(Long id) { this.id = id; }
    public void setType(RoomType type) { this.type = type; }
    public void setPricePerNight(BigDecimal pricePerNight) { this.pricePerNight = pricePerNight; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
}
