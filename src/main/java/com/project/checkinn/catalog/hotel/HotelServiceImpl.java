package com.project.checkinn.catalog.hotel;

import com.project.checkinn.ImageStorageService;
import com.project.checkinn.catalog.amenity.Amenity;
import com.project.checkinn.catalog.amenity.AmenityRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Set;

@Service
public class HotelServiceImpl implements HotelService {

    private final HotelRepo hotelRepo;
    private final AmenityRepo amenityRepo;
    private final ImageStorageService imageStorageService;

    public HotelServiceImpl(HotelRepo hotelRepo,
                            AmenityRepo amenityRepo,
                            ImageStorageService imageStorageService) {
        this.hotelRepo = hotelRepo;
        this.amenityRepo = amenityRepo;
        this.imageStorageService = imageStorageService;
    }

    @Override
    public Page<HotelResponse> getAll(Pageable pageable) {
        return hotelRepo.findAll(pageable).map(HotelMapper::toResponse);
    }

    @Override
    public HotelResponse getById(Long id) {
        Hotel h = hotelRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found"));
        return HotelMapper.toResponse(h);
    }

    @Override
    public HotelResponse create(HotelRequest req) {

        if (req == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request is required");

        if (req.getName() == null || req.getName().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is required");

        if (req.getCity() == null || req.getCity().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "City is required");

        Hotel h = new Hotel();
        h.setName(req.getName().trim());
        h.setCity(req.getCity().trim());
        h.setAddress(req.getAddress());
        h.setDescription(req.getDescription());

        if (req.getAmenityIds() != null && !req.getAmenityIds().isEmpty()) {
            Set<Amenity> amenities = new HashSet<>(amenityRepo.findAllById(req.getAmenityIds()));
            h.setAmenities(amenities);
        }

        return HotelMapper.toResponse(hotelRepo.save(h));
    }

    @Override
    public HotelResponse update(Long id, HotelRequest req) {

        if (req == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request is required");

        Hotel h = hotelRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found"));

        if (req.getName() != null && !req.getName().isBlank())
            h.setName(req.getName().trim());

        if (req.getCity() != null && !req.getCity().isBlank())
            h.setCity(req.getCity().trim());

        h.setAddress(req.getAddress());
        h.setDescription(req.getDescription());

        if (req.getAmenityIds() != null) {
            Set<Amenity> amenities = new HashSet<>(amenityRepo.findAllById(req.getAmenityIds()));
            h.setAmenities(amenities);
        }

        return HotelMapper.toResponse(hotelRepo.save(h));
    }

    @Override
    public void delete(Long id) {
        if (!hotelRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found");
        }
        hotelRepo.deleteById(id);
    }

    @Override
    public Page<HotelResponse> search(String city, String name, Long amenityId, Pageable pageable) {

        boolean hasCity = city != null && !city.isBlank();
        boolean hasName = name != null && !name.isBlank();
        boolean hasAmenity = amenityId != null;

        if (hasCity) city = city.trim();
        if (hasName) name = name.trim();

        Page<Hotel> page;

        if (hasCity && hasName && hasAmenity) {
            page = hotelRepo.findDistinctByCityIgnoreCaseAndNameContainingIgnoreCaseAndAmenities_Id(city, name, amenityId, pageable);
        } else if (hasCity && hasName) {
            page = hotelRepo.findByCityIgnoreCaseAndNameContainingIgnoreCase(city, name, pageable);
        } else if (hasCity && hasAmenity) {
            page = hotelRepo.findDistinctByCityIgnoreCaseAndAmenities_Id(city, amenityId, pageable);
        } else if (hasName && hasAmenity) {
            page = hotelRepo.findDistinctByNameContainingIgnoreCaseAndAmenities_Id(name, amenityId, pageable);
        } else if (hasCity) {
            page = hotelRepo.findByCityIgnoreCase(city, pageable);
        } else if (hasName) {
            page = hotelRepo.findByNameContainingIgnoreCase(name, pageable);
        } else if (hasAmenity) {
            page = hotelRepo.findDistinctByAmenities_Id(amenityId, pageable);
        } else {
            page = hotelRepo.findAll(pageable);
        }

        return page.map(HotelMapper::toResponse);
    }

    @Override
    public HotelResponse addAmenity(Long hotelId, Long amenityId) {

        Hotel h = hotelRepo.findById(hotelId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found"));

        Amenity a = amenityRepo.findById(amenityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Amenity not found"));

        h.addAmenity(a);

        return HotelMapper.toResponse(hotelRepo.save(h));
    }

    @Override
    public HotelResponse removeAmenity(Long hotelId, Long amenityId) {

        Hotel h = hotelRepo.findById(hotelId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found"));

        Amenity a = amenityRepo.findById(amenityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Amenity not found"));

        h.removeAmenity(a);

        return HotelMapper.toResponse(hotelRepo.save(h));
    }

    @Override
    public HotelResponse replaceAmenities(Long hotelId, Set<Long> amenityIds) {

        Hotel h = hotelRepo.findById(hotelId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found"));

        Set<Long> ids = (amenityIds == null) ? Set.of() : amenityIds;

        h.clearAmenities();

        Set<Amenity> newAmenities = new HashSet<>(amenityRepo.findAllById(ids));

        for (Amenity a : newAmenities) {
            h.addAmenity(a);
        }

        return HotelMapper.toResponse(hotelRepo.save(h));
    }

    @Override
    public String uploadImage(Long hotelId, MultipartFile file) {

        Hotel hotel = hotelRepo.findById(hotelId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found"));

        String imageUrl = imageStorageService.saveHotelImage(file);

        hotel.setImageUrl(imageUrl);
        hotelRepo.save(hotel);

        return imageUrl;
    }
}