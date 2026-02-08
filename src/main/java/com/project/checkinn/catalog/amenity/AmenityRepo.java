package com.project.checkinn.catalog.amenity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AmenityRepo extends JpaRepository<Amenity, Long> {
    boolean existsByNameIgnoreCase(String name);
}