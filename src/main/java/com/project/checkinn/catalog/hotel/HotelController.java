package com.project.checkinn.catalog.hotel;

import com.project.checkinn.catalog.amenity.Amenity;
import com.project.checkinn.catalog.amenity.AmenityRepo;
import com.project.checkinn.catalog.amenity.AmenityRequest;
import jakarta.validation.Valid;
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

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/hotels")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;

    }

    @GetMapping
    public Page<HotelResponse> all(
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return hotelService.getAll(pageable);
    }

    @GetMapping("/{id}")
    public HotelResponse one(@PathVariable Long id) {
        return hotelService.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<HotelResponse> create(
            @Valid @RequestBody HotelRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        HotelResponse saved = hotelService.create(request);

        URI location = uriBuilder
                .path("/hotels/{id}")
                .buildAndExpand(saved.getId())
                .toUri();

        return ResponseEntity.created(location).body(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<HotelResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody HotelRequest request
    ) {
        HotelResponse updated = hotelService.update(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        hotelService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public Page<HotelResponse> search(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long amenityId,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return hotelService.search(city, name, amenityId, pageable);
    }

    @PostMapping("/{id}/amenities/{amenityId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<HotelResponse> addAmenity(@PathVariable Long id, @PathVariable Long amenityId) {

        return ResponseEntity.ok(hotelService.addAmenity(id, amenityId));
    }

    @DeleteMapping("/{id}/amenities/{amenityId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<HotelResponse> removeAmenity(@PathVariable Long id, @PathVariable Long amenityId) {
        return ResponseEntity.ok(hotelService.removeAmenity(id, amenityId));
    }

    @PutMapping("/{id}/amenities")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<HotelResponse> replaceAmenities(
            @PathVariable Long id,
            @RequestBody HotelRequest req
    ) {

        return ResponseEntity.ok(hotelService.replaceAmenities(id,req.getAmenityIds()));
    }

}