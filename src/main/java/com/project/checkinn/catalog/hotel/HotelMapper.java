package com.project.checkinn.catalog.hotel;

import com.project.checkinn.catalog.room.RoomMapper;
import com.project.checkinn.catalog.room.RoomResponse;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HotelMapper {

    private HotelMapper() {}

    public static HotelResponse toResponse(Hotel h) {
        List<String> amenities = h.getAmenities().stream()
                .map(a -> a.getName())
                .toList();

        HotelResponse response = new HotelResponse(
                h.getId(),
                h.getName(),
                h.getCity(),
                h.getAddress(),
                h.getDescription(),
                amenities
        );

        response.setImageUrl(h.getImageUrl());

        return response;
    }
}