package com.project.checkinn.catalog.amenity;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/amenities")
public class AmenityController {

    private final AmenityService amenityService;

    public AmenityController(AmenityService amenityService) {
        this.amenityService = amenityService;
    }

    @GetMapping
    public List<AmenityResponse> getAll() {
        return amenityService.getAll();
    }

    @GetMapping("/{id}")
    public AmenityResponse getById(@PathVariable Long id) {
        return amenityService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AmenityResponse create(@RequestBody AmenityCreateRequest req) {
        return amenityService.create(req);
    }

    @PutMapping("/{id}")
    public AmenityResponse update(@PathVariable Long id, @RequestBody AmenityCreateRequest req) {
        return amenityService.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        amenityService.delete(id);
    }
}