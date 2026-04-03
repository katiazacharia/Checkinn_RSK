package com.project.checkinn.catalog.room;

import com.project.checkinn.catalog.amenity.AmenityResponse;

import java.util.List;

public class RoomDetailsWithAmenitiesResponse {

    private RoomResponse room;
    private List<AmenityResponse> amenities;

    public RoomDetailsWithAmenitiesResponse() {
    }

    public RoomDetailsWithAmenitiesResponse(RoomResponse room, List<AmenityResponse> amenities) {
        this.room = room;
        this.amenities = amenities;
    }

    public RoomResponse getRoom() {
        return room;
    }

    public void setRoom(RoomResponse room) {
        this.room = room;
    }

    public List<AmenityResponse> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<AmenityResponse> amenities) {
        this.amenities = amenities;
    }
}
