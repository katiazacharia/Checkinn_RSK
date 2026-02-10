package com.project.checkinn.catalog.amenity;

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
    public List<AmenityResponse> all() {
        return amenityService.getAll();
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
    @GetMapping("/{id}")
    public AmenityResponse one(@PathVariable Long id) {
        return amenityService.getById(id);
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
}
