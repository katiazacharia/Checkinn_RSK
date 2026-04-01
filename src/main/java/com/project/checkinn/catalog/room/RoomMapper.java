package com.project.checkinn.catalog.room;

import com.project.checkinn.catalog.amenity.Amenity;
import com.project.checkinn.catalog.hotel.Hotel;
import com.project.checkinn.common.CurrencyCode;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

public class RoomMapper {

    private RoomMapper() {}

    public static RoomResponse toResponse(Room r) {

        Set<String> amenityNames = r.getAmenities().stream()
                .map(Amenity::getName)
                .collect(Collectors.toSet());

        RoomResponse response = new RoomResponse(
                r.getHotel().getId(),
                r.getRoomNumber(),
                r.getType(),
                r.getPricePerNight(),
                r.getCapacity(),
                r.getStatus(),
                amenityNames
        );

        response.setCurrency(CurrencyCode.ILS.name());
        response.setOriginalPricePerNight(r.getPricePerNight());
        response.setExchangeRate(BigDecimal.ONE);
        response.setImageUrl(r.getImageUrl());

        return response;
    }
    public static Room toEntity(RoomRequest req, Hotel hotel) {
        Room r = new Room();
        r.setHotel(hotel);
        r.setRoomNumber(req.getRoomNumber().trim());
        r.setType(req.getType());
        r.setPricePerNight(req.getPricePerNight());
        r.setCapacity(req.getCapacity());
        r.setStatus(req.getStatus());
        return r;
    }
}