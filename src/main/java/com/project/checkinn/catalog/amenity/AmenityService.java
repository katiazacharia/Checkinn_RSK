package com.project.checkinn.catalog.amenity;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface AmenityService {
    Page<AmenityResponse> getAll(String name, Pageable pageable);

    List<AmenityResponse> getAll();

    AmenityResponse getById(Long id);

    AmenityResponse create(AmenityRequest req);

    AmenityResponse update(Long id, AmenityRequest req);

    void delete(Long id);

    void addAmenityToHotel(Long hotelId, Long amenityId);

    void removeAmenityFromHotel(Long hotelId, Long amenityId);

    Page<AmenityResponse> getAmenitiesForHotel(Long hotelId, Pageable pageable);

    List<AmenityResponse> getAmenitiesForHotel(Long hotelId);

    Page<AmenityResponse> getAmenitiesForHotelName(String hotelName, Pageable pageable);

}