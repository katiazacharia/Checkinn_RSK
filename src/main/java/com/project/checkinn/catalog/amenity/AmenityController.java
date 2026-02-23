package com.project.checkinn.catalog.amenity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/amenities")
public class AmenityController {

     private final AmenityService amenityService;

    public AmenityController(AmenityService amenityService) {
        this.amenityService = amenityService;
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
    public ResponseEntity<AmenityResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody AmenityRequest request
    ) {
        AmenityResponse updated = amenityService.update(id, request);
        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("/{id}")
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


    @PostMapping("/{amenityId}/hotel/{hotelId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addAmenityToHotel(@PathVariable Long amenityId, @PathVariable Long hotelId) {
        amenityService.addAmenityToHotel(hotelId, amenityId);
    }


    @DeleteMapping("/{amenityId}/hotel/{hotelId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAmenityFromHotel(@PathVariable Long amenityId, @PathVariable Long hotelId) {
        amenityService.removeAmenityFromHotel(hotelId, amenityId);
    }
}
