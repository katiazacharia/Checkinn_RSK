package com.project.checkinn.catalog.hotel;

import com.project.checkinn.catalog.amenity.AmenityMapper;
import com.project.checkinn.catalog.amenity.AmenityResponse;
import com.project.checkinn.catalog.amenity.AmenityService;
import com.project.checkinn.catalog.room.Room;
import com.project.checkinn.catalog.room.RoomResponse;
import com.project.checkinn.catalog.room.RoomService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/hotels")
public class HotelController {

    private final HotelService hotelService;
    private final RoomService roomService;
    private final AmenityService amenityService;

    public HotelController(HotelService hotelService, RoomService roomService, AmenityService amenityService) {
        this.hotelService = hotelService;
        this.roomService = roomService;
        this.amenityService = amenityService;
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

      @PostMapping("/{id}/image")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<String> uploadHotelImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        String imageUrl = hotelService.uploadImage(id, file);
        return ResponseEntity.ok(imageUrl);
    }

    @GetMapping("/{hotelName}/rooms")
    public Page<RoomResponse> getRoomsByHotelName(
            @PathVariable String hotelName,
            @PageableDefault(page = 0, size = 10, sort = "roomNumber") Pageable pageable
    ) {
        return roomService.getRoomsByHotelName(hotelName, pageable);
    }

    @GetMapping("/{hotelName}/amenities")
    public Page<AmenityResponse> getAmenitiesByHotelName(
            @PathVariable String hotelName,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return amenityService.getAmenitiesForHotelName(hotelName, pageable);
    }


}