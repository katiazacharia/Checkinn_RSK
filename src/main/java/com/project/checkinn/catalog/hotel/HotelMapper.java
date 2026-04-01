package com.project.checkinn.catalog.hotel;

import com.project.checkinn.catalog.room.RoomMapper;
import com.project.checkinn.catalog.room.RoomResponse;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HotelMapper {

    private HotelMapper() {}

    public static HotelResponse toResponse(Hotel h) {
        List<RoomResponse> rooms = h.getRooms().stream()
                .map(RoomMapper::toResponse)
                .toList();

        List<String> amenities = h.getAmenities().stream()
                .map(a -> a.getName())
                .toList();

        return new HotelResponse(
                h.getId(),
                h.getName(),
                h.getCity(),
                h.getAddress(),
                h.getDescription(),
                amenities
        );
    }
}