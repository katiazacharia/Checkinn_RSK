package com.project.checkinn.catalog.amenity;

import com.project.checkinn.catalog.hotel.Hotel;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;




@Service
public class AmenityServiceImpl implements AmenityService {

    private final AmenityRepo amenityRepo;
    private final EntityManager entityManager;

    public AmenityServiceImpl(AmenityRepo amenityRepo, EntityManager entityManager) {
        this.amenityRepo = amenityRepo;
        this.entityManager = entityManager;
    }

    @Override
    public Page<AmenityResponse> getAll(String name, Pageable pageable) {

        Page<Amenity> page;

        if (name != null && !name.isBlank()) {
            page = amenityRepo.findByNameContainingIgnoreCase(name.trim(), pageable);
        } else {
            page = amenityRepo.findAll(pageable);
        }

        return page.map(this::toResponse);
    }

    @Override
    public List<AmenityResponse> getAll() {
        Pageable defaultPageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Direction.DESC, "id"));
        return getAll(null, defaultPageable).getContent();
    }

    @Override
    public AmenityResponse getById(Long id) {
        Amenity a = amenityRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Amenity not found"));
        return toResponse(a);
    }

    @Override
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

    @Override
    public AmenityResponse update(Long id, AmenityRequest req) {

        Amenity a = amenityRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Amenity not found"));

        if (req.getName() != null && !req.getName().isBlank()) {
            String newName = req.getName().trim();

            if (!newName.equalsIgnoreCase(a.getName()) &&
                    amenityRepo.existsByNameIgnoreCase(newName))

                throw new ResponseStatusException(HttpStatus.CONFLICT, "Amenity name already exists");

            a.setName(newName);
        }

        a.setIcon(req.getIcon());
        a.setDescription(req.getDescription());

        return toResponse(amenityRepo.save(a));
    }

    @Override
    public void delete(Long id) {

        if (!amenityRepo.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Amenity not found");

        amenityRepo.deleteById(id);
    }

    @Override
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

    @Override
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

    @Override
    public Page<AmenityResponse> getAmenitiesForHotel(Long hotelId, Pageable pageable) {

        Hotel hotel = entityManager.find(Hotel.class, hotelId);

        if (hotel == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found");

        return amenityRepo.findByHotels_Id(hotelId, pageable)
                .map(this::toResponse);
    }

    @Override
    public List<AmenityResponse> getAmenitiesForHotel(Long hotelId) {

        Pageable defaultPageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Direction.DESC, "id"));

        return getAmenitiesForHotel(hotelId, defaultPageable).getContent();
    }

    private AmenityResponse toResponse(Amenity a) {

        Set<Long> hotelIds = a.getHotels().stream()
                .map(Hotel::getId)
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
