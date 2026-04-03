package com.project.checkinn.catalog.amenity;

public class AmenityHotelItemResponse {

    private Long hotelId;
    private String hotelName;

    public AmenityHotelItemResponse(Long hotelId, String hotelName) {
        this.hotelId = hotelId;
        this.hotelName = hotelName;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public String getHotelName() {
        return hotelName;
    }
}
