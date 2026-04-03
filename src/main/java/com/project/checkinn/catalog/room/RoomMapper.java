package com.project.checkinn.catalog.room;

import com.project.checkinn.catalog.amenity.Amenity;
import com.project.checkinn.catalog.hotel.Hotel;
import com.project.checkinn.common.CurrencyCode;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

public class RoomMapper {

    private RoomMapper() {}

    public static RoomSummaryResponse toSummaryResponse(Room room) {
        return new RoomSummaryResponse(
                room.getId(),
                room.getType(),
                room.getPricePerNight(),
                room.getCapacity()
        );
    }

    public static RoomResponse toResponse(Room room) {
        return new RoomResponse(
                room.getId(),
                room.getHotel().getId(),
                room.getType(),
                room.getRoomNumber(),
                room.getPricePerNight(),
                room.getCapacity(),
                room.getStatus(),
                room.getImageUrls()
        );
    }

    public static Room toEntity(RoomRequest req, Hotel hotel) {
        Room room = new Room();
        room.setHotel(hotel);
        room.setRoomNumber(req.getRoomNumber());
        room.setType(req.getType());
        room.setPricePerNight(req.getPricePerNight());
        room.setCapacity(req.getCapacity());
        room.setStatus(req.getStatus());
        return room;
    }
}