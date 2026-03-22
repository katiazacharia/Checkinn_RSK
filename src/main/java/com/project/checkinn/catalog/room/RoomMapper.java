package com.project.checkinn.catalog.room;

import com.project.checkinn.catalog.hotel.Hotel;

import java.math.BigDecimal;

public class RoomMapper {


    private RoomMapper() {}

    public static RoomResponse toResponse(Room r) {
       RoomResponse response = new RoomResponse(
               r.getId(),
               r.getHotel().getId(),
               r.getRoomNumber(),
               r.getType(),
               r.getPricePerNight(),
               r.getCapacity(),
               r.getStatus()

       );
       response.setCurrency("ILS");
       response.setOriginalPricePerNight(r.getPricePerNight());
       response.setExchangeRate(BigDecimal.ONE);

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
