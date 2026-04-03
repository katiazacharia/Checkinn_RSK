package com.project.checkinn.catalog.hotel;

import java.util.List;

public class HotelMapper {

    private HotelMapper() {}


    public static HotelListResponse toListResponse(Hotel h) {
        return new HotelListResponse(
                h.getId(),
                h.getName()
        );
    }

    public static HotelDetailsResponse toDetailsResponse(Hotel h) {
        return new HotelDetailsResponse(
                h.getId(),
                h.getName(),
                h.getCity(),
                h.getAddress(),
                h.getDescription(),
                h.getImageUrls()
        );
    }
}