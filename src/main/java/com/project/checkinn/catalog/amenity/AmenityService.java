package com.project.checkinn.catalog.amenity;

import com.project.checkinn.catalog.hotel.Hotel;
import jakarta.persistence.EntityManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AmenityService {

    private final AmenityRepo amenityRepo;
    private final EntityManager entityManager;

    public AmenityService(AmenityRepo amenityRepo, EntityManager entityManager) {
        this.amenityRepo = amenityRepo;
        this.entityManager = entityManager;
    }

    public List<AmenityResponse> getAll() {
        return amenityRepo.findAll().stream().map(this::toResponse).toList();
    }

    public AmenityResponse getById(Long id) {
        Amenity a = amenityRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Amenity not found"));
        return toResponse(a);
    }

    public AmenityResponse create(AmenityRequest req) {
        if (req.getName() == null || req.getName().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is required");

        String name = req.getName().trim();

        if (amenityRepo.existsByNameIgnoreCase(name))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Amenity name already exists");

        Amenity a = new Amenity();
        a.setName(name);
        a.setIcon(req.getIcon());
        a.setDescription(req.getDescription());

        return toResponse(amenityRepo.save(a));
    }

    public AmenityResponse update(Long id, AmenityRequest req) {
        Amenity a = amenityRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Amenity not found"));

        if (req.getName() != null && !req.getName().isBlank()) {
            String newName = req.getName().trim();
            if (!newName.equalsIgnoreCase(a.getName()) && amenityRepo.existsByNameIgnoreCase(newName))
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Amenity name already exists");
            a.setName(newName);
        }

        a.setIcon(req.getIcon());
        a.setDescription(req.getDescription());

        return toResponse(amenityRepo.save(a));
    }

    public void delete(Long id) {
        if (!amenityRepo.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Amenity not found");
        amenityRepo.deleteById(id);
    }


    @Transactional
    public void addAmenityToHotel(Long hotelId, Long amenityId) {
        Hotel hotel = entityManager.find(Hotel.class, hotelId);
        if (hotel == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found");

        Amenity amenity = amenityRepo.findById(amenityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Amenity not found"));

        hotel.getAmenities().add(amenity);
        amenity.getHotels().add(hotel);

    }


    @Transactional
    public void removeAmenityFromHotel(Long hotelId, Long amenityId) {
        Hotel hotel = entityManager.find(Hotel.class, hotelId);
        if (hotel == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found");

        Amenity amenity = amenityRepo.findById(amenityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Amenity not found"));

        hotel.getAmenities().remove(amenity);
        amenity.getHotels().remove(hotel);
    }

    public List<AmenityResponse> getAmenitiesForHotel(Long hotelId) {
        Hotel hotel = entityManager.find(Hotel.class, hotelId);
        if (hotel == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found");

        return hotel.getAmenities().stream().map(this::toResponse).toList();
    }

    private AmenityResponse toResponse(Amenity a) {
        Set<Long> hotelIds = a.getHotels().stream()
                .map(h -> h.getId())
                .collect(Collectors.toSet());

        return new AmenityResponse(a.getId(), a.getName(), a.getIcon(), a.getDescription(), hotelIds);
    }
}