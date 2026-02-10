package com.project.checkinn.catalog.amenity;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AmenityService {

    private final AmenityRepo amenityRepo;

    public AmenityService(AmenityRepo amenityRepo) {
        this.amenityRepo = amenityRepo;
    }

    public List<AmenityResponse> getAll() {
        return amenityRepo.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public AmenityResponse getById(Long id) {
        Amenity amenity = amenityRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Amenity not found"));
        return toResponse(amenity);
    }

    public AmenityResponse create(AmenityRequest req) {
        if (req.getName() == null || req.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is required");
        }
        if (amenityRepo.existsByNameIgnoreCase(req.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Amenity name already exists");
        }

        Amenity a = new Amenity();
        a.setName(req.getName().trim());
        a.setIcon(req.getIcon());
        a.setDescription(req.getDescription());

        return toResponse(amenityRepo.save(a));
    }

    public AmenityResponse update(Long id, AmenityRequest req) {
        Amenity a = amenityRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Amenity not found"));

        if (req.getName() != null && !req.getName().isBlank()) {
            String newName = req.getName().trim();
            if (!newName.equalsIgnoreCase(a.getName()) && amenityRepo.existsByNameIgnoreCase(newName)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Amenity name already exists");
            }
            a.setName(newName);
        }

        a.setIcon(req.getIcon());
        a.setDescription(req.getDescription());

        return toResponse(amenityRepo.save(a));
    }

    public void delete(Long id) {
        if (!amenityRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Amenity not found");
        }
        amenityRepo.deleteById(id);
    }

    private AmenityResponse toResponse(Amenity a) {
        Set<Long> hotelIds = a.getHotels().stream()
                .map(h -> h.getId())
                .collect(Collectors.toSet());

        return new AmenityResponse(
                a.getId(),
                a.getName(),
                a.getIcon(),
                a.getDescription(),
                hotelIds
        );
    }
}