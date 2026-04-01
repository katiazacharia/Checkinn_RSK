package com.project.checkinn.catalog.amenity;

import com.project.checkinn.catalog.hotel.Hotel;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/amenities")
public class AmenityController {

     private final AmenityService amenityService;
    private final EntityManager entityManager;

    public AmenityController(AmenityService amenityService, EntityManager entityManager) {
        this.amenityService = amenityService;
        this.entityManager = entityManager;
    }

    @GetMapping
    public Page<AmenityResponse> all(
            @RequestParam(required = false) String name,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return amenityService.getAll(name, pageable);
    }

    @GetMapping("/{id}")
    public AmenityResponse getById(@PathVariable Long id) {

        return amenityService.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<AmenityResponse> create(
            @Valid @RequestBody AmenityRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        AmenityResponse saved = amenityService.create(request);

        URI location = uriBuilder
                .path("/amenities/{id}")
                .buildAndExpand(saved.getId())
                .toUri();

        return ResponseEntity.created(location).body(saved);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<AmenityResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody AmenityRequest request
    ) {
        AmenityResponse updated = amenityService.update(id, request);
        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        amenityService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/hotel/{hotelId}")
    public Page<AmenityResponse> getAmenitiesForHotel(
            @PathVariable Long hotelId,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return amenityService.getAmenitiesForHotel(hotelId, pageable);
    }
    @GetMapping("/hotel/{hotelId}/with-name")
    public Map<String, Object> getAmenitiesForHotelWithHotelName(
            @PathVariable Long hotelId,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable) {


        Page<AmenityResponse> amenities = amenityService.getAmenitiesForHotel(hotelId, pageable);


        Hotel hotel = entityManager.find(Hotel.class, hotelId);
        if (hotel == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found");
        }


        Map<String, Object> response = new HashMap<>();
        response.put("hotelName", hotel.getName());
        response.put("amenities", amenities);

        return response;
    }


    @PostMapping("/{amenityId}/hotel/{hotelId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addAmenityToHotel(@PathVariable Long amenityId, @PathVariable Long hotelId) {
        amenityService.addAmenityToHotel(hotelId, amenityId);
    }


    @DeleteMapping("/{amenityId}/hotel/{hotelId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAmenityFromHotel(@PathVariable Long amenityId, @PathVariable Long hotelId) {
        amenityService.removeAmenityFromHotel(hotelId, amenityId);
    }
}
