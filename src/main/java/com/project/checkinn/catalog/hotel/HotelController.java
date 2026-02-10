package com.project.checkinn.catalog.hotel;

import com.project.checkinn.catalog.amenity.Amenity;
import com.project.checkinn.catalog.amenity.AmenityRepo;
import com.project.checkinn.catalog.amenity.AmenityRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final HotelRepo hotelRepo;
    private final AmenityRepo amenityRepo;

    public HotelController(HotelService hotelService, HotelRepo hotelRepo, AmenityRepo amenityRepo) {
        this.hotelService = hotelService;
        this.hotelRepo = hotelRepo;
        this.amenityRepo = amenityRepo;
    }

    @GetMapping
    public List<HotelResponse> all() {
        return hotelService.getAll();
    }

    @GetMapping("/{id}")
    public HotelResponse one(@PathVariable Long id) {
        return hotelService.getById(id);
    }
    @PostMapping
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
    public ResponseEntity<HotelResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody HotelRequest request
    ) {
        HotelResponse updated = hotelService.update(id, request);
        return ResponseEntity.ok(updated);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        hotelService.delete(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/search")
    public List<HotelResponse> search(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long amenityId
    ) {
        List<Hotel> hotels = hotelRepo.findAll();

        return hotels.stream()
                .filter(h -> city == null || (h.getCity() != null && h.getCity().equalsIgnoreCase(city)))
                .filter(h -> name == null || (h.getName() != null && h.getName().toLowerCase().contains(name.toLowerCase())))
                .filter(h -> {
                    if (amenityId == null) return true;
                    return h.getAmenities().stream().anyMatch(a -> a.getId().equals(amenityId));
                })
                .map(this::toResponse)
                .toList();
    }


   @PostMapping("/{id}/amenities/{amenityId}")
    public ResponseEntity<HotelResponse> addAmenity(@PathVariable Long id, @PathVariable Long amenityId) {
        Hotel h = hotelRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found"));

        Amenity a = amenityRepo.findById(amenityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Amenity not found"));

        h.getAmenities().add(a);
        Hotel saved = hotelRepo.save(h);

        return ResponseEntity.ok(toResponse(saved));
    }
    @DeleteMapping("/{id}/amenities/{amenityId}")
    public ResponseEntity<HotelResponse> removeAmenity(@PathVariable Long id, @PathVariable Long amenityId) {
        Hotel h = hotelRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found"));

        h.getAmenities().removeIf(am -> am.getId().equals(amenityId));
        Hotel saved = hotelRepo.save(h);

        return ResponseEntity.ok(toResponse(saved));
    }


    @PutMapping("/{id}/amenities")
    public ResponseEntity<HotelResponse> replaceAmenities(
            @PathVariable Long id,
            @RequestBody HotelRequest req
    ) {
        Hotel h = hotelRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Hotel not found"
                ));

        Set<Long> ids = (req.getAmenityIds() == null)
                ? Set.of()
                : req.getAmenityIds();

        Set<Amenity> amenities = new HashSet<>(
                amenityRepo.findAllById(ids)
        );

        h.setAmenities(amenities);

        return ResponseEntity.ok(toResponse(hotelRepo.save(h)));
    }


    private HotelResponse toResponse(Hotel h) {
        Set<Long> roomIds = h.getRooms().stream().map(r -> r.getId()).collect(Collectors.toSet());
        Set<Long> amenityIds = h.getAmenities().stream().map(a -> a.getId()).collect(Collectors.toSet());

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

}