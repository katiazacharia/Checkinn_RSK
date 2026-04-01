package com.project.checkinn.catalog.amenity;

import com.project.checkinn.catalog.hotel.Hotel;
import com.project.checkinn.catalog.hotel.HotelRepo;
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

import static com.project.checkinn.catalog.amenity.AmenityMapper.toResponse;
import static org.springframework.http.HttpStatus.NOT_FOUND;


@Service
public class AmenityServiceImpl implements AmenityService {

    private final AmenityRepo amenityRepo;
    private final EntityManager entityManager;
    private final HotelRepo hotelRepository;


    public AmenityServiceImpl(AmenityRepo amenityRepo, EntityManager entityManager, HotelRepo hotelRepository) {
        this.amenityRepo = amenityRepo;
        this.entityManager = entityManager;
        this.hotelRepository = hotelRepository;
    }

    @Override
    public Page<AmenityResponse> getAll(String name, Pageable pageable) {

        Page<Amenity> page;

        if (name != null && !name.isBlank()) {
            page = amenityRepo.findByNameContainingIgnoreCase(name.trim(), pageable);
        } else {
            page = amenityRepo.findAll(pageable);
        }

        return page.map(AmenityMapper::toResponse);
    }

    @Override
    public List<AmenityResponse> getAll() {
        Pageable defaultPageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Direction.DESC, "id"));
        return getAll(null, defaultPageable).getContent();
    }

    @Override
    public AmenityResponse getById(Long id) {
        Amenity a = amenityRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Amenity not found"));
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
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Amenity not found"));

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
            throw new ResponseStatusException(NOT_FOUND, "Amenity not found");

        amenityRepo.deleteById(id);
    }

    @Override
    @Transactional
    public void addAmenityToHotel(Long hotelId, Long amenityId) {

        Hotel hotel = entityManager.find(Hotel.class, hotelId);

        if (hotel == null)
            throw new ResponseStatusException(NOT_FOUND, "Hotel not found");

        Amenity amenity = amenityRepo.findById(amenityId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Amenity not found"));

        hotel.getAmenities().add(amenity);
        amenity.getHotels().add(hotel);
    }

    @Override
    @Transactional
    public void removeAmenityFromHotel(Long hotelId, Long amenityId) {

        Hotel hotel = entityManager.find(Hotel.class, hotelId);

        if (hotel == null)
            throw new ResponseStatusException(NOT_FOUND, "Hotel not found");

        Amenity amenity = amenityRepo.findById(amenityId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Amenity not found"));

        hotel.getAmenities().remove(amenity);
        amenity.getHotels().remove(hotel);
    }

    @Override
    public Page<AmenityResponse> getAmenitiesForHotel(Long hotelId, Pageable pageable) {

        Hotel hotel = entityManager.find(Hotel.class, hotelId);

        if (hotel == null)
            throw new ResponseStatusException(NOT_FOUND, "Hotel not found");

        return amenityRepo.findByHotels_Id(hotelId, pageable)
                .map(a -> AmenityMapper.toResponseWithoutHotels(a));
    }

    @Override
    public List<AmenityResponse> getAmenitiesForHotel(Long hotelId) {

        Pageable defaultPageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Direction.DESC, "id"));

        return getAmenitiesForHotel(hotelId, defaultPageable).getContent();
    }

    @Override
    public Page<AmenityResponse> getAmenitiesForHotelName(String hotelName, Pageable pageable) {
        Hotel hotel = hotelRepository.findByNameIgnoreCase(hotelName)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Hotel not found"));


        return amenityRepo.findByHotels_Id(hotel.getId(), pageable)
                .map(a -> AmenityMapper.toResponseWithoutHotels(a));
    }



}
