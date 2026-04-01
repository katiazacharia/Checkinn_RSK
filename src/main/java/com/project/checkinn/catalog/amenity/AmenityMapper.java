package com.project.checkinn.catalog.amenity;

import java.util.Set;
import java.util.stream.Collectors;

public class AmenityMapper {

    private AmenityMapper() {}




    public static AmenityResponse toResponse(Amenity a) {

        Set<String> hotelNames = a.getHotels().stream()
                .map(h -> h.getName())
                .collect(Collectors.toSet());


        return new AmenityResponse(
                a.getId(),
                a.getName(),
                a.getIcon(),
                a.getDescription(),
                hotelNames
        );
    }


    public static AmenityResponse toResponseWithoutHotels(Amenity a) {
        return new AmenityResponse(
                a.getId(),
                a.getName(),
                a.getIcon(),
                a.getDescription(),
                null
        );
    }

    public static Amenity toEntity(AmenityRequest req) {
        Amenity a = new Amenity();
        a.setName(req.getName());
        a.setIcon(req.getIcon());
        a.setDescription(req.getDescription());
        return a;
    }
}
