package com.project.checkinn.catalog.amenity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AmenityRepo extends JpaRepository<Amenity, Long> {
    boolean existsByNameIgnoreCase(String name);
    Optional<Amenity> findByNameIgnoreCase(String name);
}