package com.project.checkinn.catalog.amenity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AmenityRepo extends JpaRepository<Amenity, Long> {
    boolean existsByNameIgnoreCase(String name);
    Page<Amenity> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Amenity> findByHotels_Id(Long hotelId, Pageable pageable);

}