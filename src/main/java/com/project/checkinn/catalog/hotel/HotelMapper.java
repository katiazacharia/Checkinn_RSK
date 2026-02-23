package com.project.checkinn.catalog.hotel;

import java.util.Set;
import java.util.stream.Collectors;

public class HotelMapper {

    private HotelMapper() {}

    public static HotelResponse toResponse(Hotel h) {
        Set<Long> roomIds = h.getRooms().stream()
                .map(r -> r.getId())
                .collect(Collectors.toSet());

        Set<Long> amenityIds = h.getAmenities().stream()
                .map(a -> a.getId())
                .collect(Collectors.toSet());

        return new HotelResponse(
                h.getId(),
                h.getName(),
                h.getCity(),
                h.getAddress(),
                h.getDescription(),
                roomIds,
                amenityIds
        );
    }

    public static void updateEntity(Hotel h, HotelRequest req) {
        if (req.getName() != null && !req.getName().isBlank()) h.setName(req.getName().trim());
        if (req.getCity() != null && !req.getCity().isBlank()) h.setCity(req.getCity().trim());
        h.setAddress(req.getAddress());
        h.setDescription(req.getDescription());
    }
}
