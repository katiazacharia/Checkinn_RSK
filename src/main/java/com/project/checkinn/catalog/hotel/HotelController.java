package com.project.checkinn.catalog.hotel;

import com.project.checkinn.catalog.amenity.AmenityResponse;
import com.project.checkinn.catalog.amenity.AmenityService;
import com.project.checkinn.catalog.room.RoomDetailsWithAmenitiesResponse;
import com.project.checkinn.catalog.room.RoomResponse;
import com.project.checkinn.catalog.room.RoomService;
import com.project.checkinn.catalog.room.RoomSummaryResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

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
    public Page<HotelListResponse> all(
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return hotelService.getAll(pageable);
    }

    @GetMapping("/{id}")
    public HotelDetailsResponse one(@PathVariable Long id) {
        return hotelService.getById(id);
    }
    @GetMapping("/name/{hotelName}")
    public HotelDetailsResponse getByName(@PathVariable String hotelName) {
        return hotelService.getByName(hotelName);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<HotelDetailsResponse> create(
            @Valid @RequestBody HotelRequest request,
            Authentication authentication,
            UriComponentsBuilder uriBuilder
    ) {
        HotelDetailsResponse saved = hotelService.create(request,authentication);

        URI location = uriBuilder
                .path("/hotels/{id}")
                .buildAndExpand(saved.getId())
                .toUri();

        return ResponseEntity.created(location).body(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('MANAGER') and @authz.isHotelManager(#id, authentication))")
    public ResponseEntity<HotelDetailsResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody HotelRequest request
    ) {
        HotelDetailsResponse updated = hotelService.update(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('MANAGER') and @authz.isHotelManager(#id, authentication))")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        hotelService.delete(id);
        return ResponseEntity.noContent().build();
    }


      @PostMapping("/{id}/image")
      @PreAuthorize("hasRole('ADMIN') or (hasRole('MANAGER') and @authz.isHotelManager(#id, authentication))")
      public ResponseEntity<String> uploadHotelImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        String imageUrl = hotelService.uploadImage(id, file);
        return ResponseEntity.ok(imageUrl);
    }

    //hotels/hotelId/rooms in that hotel but not detailed  this is for admin/manager since they know the ids
    @GetMapping("/{hotelId}/rooms")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('MANAGER') and @authz.isHotelManager(#hotelId, authentication))")
    public Page<RoomSummaryResponse> getRoomsByHotelId(
            @PathVariable Long hotelId,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return roomService.getRoomsByHotelId(hotelId, pageable);
    }

    //hotels/hotelName/ rooms in that hotel not in details this is for customers becuas they don't know the ids
    @GetMapping("/name/{hotelName}/rooms")
    public Page<RoomSummaryResponse> getRoomsByHotelName(
            @PathVariable String hotelName,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return roomService.getRoomsByHotelName(hotelName, pageable);
    }

    //same for amenity
    @GetMapping("/{hotelId}/amenities")
    public Page<AmenityResponse> getAmenitiesByHotelId(
            @PathVariable Long hotelId,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return amenityService.getAmenitiesForHotel(hotelId, pageable);
    }

    @GetMapping("/name/{hotelName}/amenities")
    public Page<AmenityResponse> getAmenitiesByHotelName(
            @PathVariable String hotelName,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return amenityService.getAmenitiesForHotelName(hotelName, pageable);
    }


    @GetMapping("/name/{hotelName}/rooms/{roomId}")
    public RoomDetailsWithAmenitiesResponse getRoomDetailsByHotelName(
            @PathVariable String hotelName,
            @PathVariable Long roomId
    ) {
        return roomService.getRoomDetailsByHotelName(hotelName, roomId);
    }

    @GetMapping("/{hotelId}/rooms/{roomId}")
    public RoomDetailsWithAmenitiesResponse getRoomDetailsByHotelId(
            @PathVariable Long hotelId,
            @PathVariable Long roomId
    ) {
        return roomService.getRoomDetailsByHotelId(hotelId, roomId);
    }
}