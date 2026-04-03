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


    @PostMapping("/{amenityId}/room/{roomId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addAmenityToRoom(@PathVariable Long amenityId, @PathVariable Long roomId) {
        amenityService.addAmenityToRoom(roomId, amenityId);
    }

    @DeleteMapping("/{amenityId}/room/{roomId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAmenityFromRoom(@PathVariable Long amenityId, @PathVariable Long roomId) {
        amenityService.removeAmenityFromRoom(roomId, amenityId);
    }
}
