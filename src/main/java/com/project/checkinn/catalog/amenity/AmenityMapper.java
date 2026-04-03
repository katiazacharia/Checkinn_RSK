package com.project.checkinn.catalog.amenity;

import com.project.checkinn.catalog.room.Room;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AmenityMapper {

    private AmenityMapper() {}



    public static AmenityResponse toResponse(Amenity amenity) {
        return new AmenityResponse(
                amenity.getId(),
                amenity.getName(),
                amenity.getIcon(),
                amenity.getDescription(),
                amenity.getType()
        );
    }

}
