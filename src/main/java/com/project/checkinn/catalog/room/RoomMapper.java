package com.project.checkinn.catalog.room;

import com.project.checkinn.catalog.hotel.Hotel;

public class RoomMapper {


    private RoomMapper() {}

    public static RoomResponse toResponse(Room r) {
        return new RoomResponse(
                r.getId(),
                r.getHotel().getId(),
                r.getRoomNumber(),
                r.getType(),
                r.getPricePerNight(),
                r.getCapacity(),
                r.getStatus()
        );
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
