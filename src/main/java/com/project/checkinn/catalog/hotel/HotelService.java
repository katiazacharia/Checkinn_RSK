package com.project.checkinn.catalog.hotel;

import com.project.checkinn.catalog.amenity.Amenity;
import com.project.checkinn.catalog.amenity.AmenityRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class HotelService {

    private final HotelRepo hotelRepo;
    private final AmenityRepo amenityRepo;

    public HotelService(HotelRepo hotelRepo, AmenityRepo amenityRepo) {
        this.hotelRepo = hotelRepo;
        this.amenityRepo = amenityRepo;
    }

    public List<HotelResponse> getAll() {
        return hotelRepo.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public HotelResponse getById(Long id) {
        Hotel h = hotelRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found"));
        return toResponse(h);
    }

    public HotelResponse create(HotelRequest req) {
        if (req.getName() == null || req.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is required");
        }
        if (req.getCity() == null || req.getCity().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "City is required");
        }

        Hotel h = new Hotel();
        h.setName(req.getName().trim());
        h.setCity(req.getCity().trim());
        h.setAddress(req.getAddress());
        h.setDescription(req.getDescription());


        if (req.getAmenityIds() != null && !req.getAmenityIds().isEmpty()) {
            Set<Amenity> amenities = new HashSet<>(amenityRepo.findAllById(req.getAmenityIds()));
            h.setAmenities(amenities);
        }

        return toResponse(hotelRepo.save(h));
    }

    public HotelResponse update(Long id, HotelRequest req) {
        Hotel h = hotelRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found"));

        if (req.getName() != null && !req.getName().isBlank()) h.setName(req.getName().trim());
        if (req.getCity() != null && !req.getCity().isBlank()) h.setCity(req.getCity().trim());

        h.setAddress(req.getAddress());
        h.setDescription(req.getDescription());

        if (req.getAmenityIds() != null) {
            Set<Amenity> amenities = new HashSet<>(amenityRepo.findAllById(req.getAmenityIds()));
            h.setAmenities(amenities);
        }

        return toResponse(hotelRepo.save(h));
    }

    public void delete(Long id) {
        if (!hotelRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found");
        }
        hotelRepo.deleteById(id);
    }

    private HotelResponse toResponse(Hotel h) {
        Set<Long> roomIds = h.getRooms().stream()
                .map(r -> r.getId())
                .collect(Collectors.toSet());

        Set<Long> amenityIds = h.getAmenities().stream()
                .map(a -> a.getId())
                .collect(Collectors.toSet());

        return new HotelResponse(
                h.getId(),
                h.getName(),
                h.getCity(),
                h.getAddress(),
                h.getDescription(),
                roomIds,
                amenityIds
        );
    }

    public List<HotelResponse> search(String city, String name, Long amenityId) {
        List<Hotel> hotels = hotelRepo.findAll();

        return hotels.stream()
                .filter(h -> city == null || (h.getCity() != null && h.getCity().equalsIgnoreCase(city)))
                .filter(h -> name == null || (h.getName() != null && h.getName().toLowerCase().contains(name.toLowerCase())))
                .filter(h -> amenityId == null || h.getAmenities().stream().anyMatch(a -> a.getId().equals(amenityId)))
                .map(this::toResponse)
                .toList();
    }


    public HotelResponse addAmenity(Long hotelId, Long amenityId) {
        Hotel h = hotelRepo.findById(hotelId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found"));

        Amenity a = amenityRepo.findById(amenityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Amenity not found"));

        h.addAmenity(a);
        return toResponse(hotelRepo.save(h));
    }


    public HotelResponse removeAmenity(Long hotelId, Long amenityId) {
        Hotel h = hotelRepo.findById(hotelId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found"));

        Amenity a = amenityRepo.findById(amenityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Amenity not found"));

        h.removeAmenity(a);
        return toResponse(hotelRepo.save(h));
    }

    public HotelResponse replaceAmenities(Long hotelId, Set<Long> amenityIds) {
        Hotel h = hotelRepo.findById(hotelId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found"));

        Set<Long> ids = (amenityIds == null) ? Set.of() : amenityIds;


        h.clearAmenities();


        Set<Amenity> newAmenities = new HashSet<>(amenityRepo.findAllById(ids));
        for (Amenity a : newAmenities) {
            h.addAmenity(a);
        }

        return toResponse(hotelRepo.save(h));
    }
}