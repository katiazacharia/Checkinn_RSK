package com.project.checkinn.catalog.room;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;



    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public List<RoomResponse> all() {
        return roomService.getAll();
    }


    @GetMapping("/{id}")
    public RoomResponse one(@PathVariable Long id) {
        return roomService.getById(id);
    }


    @GetMapping("/hotel/{hotelId}")
    public List<RoomResponse> byHotel(@PathVariable Long hotelId) {
        return roomService.getByHotel(hotelId);
    }
    @PostMapping
    public ResponseEntity<RoomResponse> create(
            @Valid @RequestBody RoomRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        RoomResponse saved = roomService.create(request);

        URI location = uriBuilder
                .path("/rooms/{id}")
                .buildAndExpand(saved.getId())
                .toUri();

        return ResponseEntity.created(location).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody RoomRequest request
    ) {
        RoomResponse updated = roomService.update(id, request);
        return ResponseEntity.ok(updated);
    }
    }




