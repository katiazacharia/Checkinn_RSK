package com.project.checkinn.catalog.hotel;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface HotelRepo extends JpaRepository<Hotel, Long> {

    Page<Hotel> findByCityIgnoreCase(String city, Pageable pageable);


    Page<Hotel> findByNameContainingIgnoreCase(String name, Pageable pageable);


    Page<Hotel> findDistinctByAmenities_Id(Long amenityId, Pageable pageable);

    Page<Hotel> findByCityIgnoreCaseAndNameContainingIgnoreCase(String city, String name, Pageable pageable);

    Page<Hotel> findDistinctByCityIgnoreCaseAndAmenities_Id(String city, Long amenityId, Pageable pageable);

    Page<Hotel> findDistinctByNameContainingIgnoreCaseAndAmenities_Id(String name, Long amenityId, Pageable pageable);

    Page<Hotel> findDistinctByCityIgnoreCaseAndNameContainingIgnoreCaseAndAmenities_Id(
            String city, String name, Long amenityId, Pageable pageable
    );
    Optional<Hotel> findByNameIgnoreCase(String name);
}