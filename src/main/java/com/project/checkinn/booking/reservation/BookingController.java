package com.project.checkinn.booking.reservation;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }


    @GetMapping
    public List<BookingResponse> all() {
        return bookingService.getAll()
                .stream()
                .map(BookingMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public BookingResponse one(@PathVariable Long id) {
        return BookingMapper.toResponse(bookingService.getById(id));
    }

    @GetMapping("/user/{userId}")
    public List<BookingResponse> byUser(@PathVariable Long userId) {
        return bookingService.getByUser(userId)
                .stream()
                .map(BookingMapper::toResponse)
                .toList();
    }

    @PostMapping
    public ResponseEntity<BookingResponse> create(
            @RequestBody BookingRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        Booking saved = bookingService.create(request);
        BookingResponse response = BookingMapper.toResponse(saved);

        URI location = uriBuilder
                .path("/bookings/{id}")
                .buildAndExpand(saved.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancel(@PathVariable Long id) {
        Booking cancelled = bookingService.cancel(id);
        return ResponseEntity.ok(BookingMapper.toResponse(cancelled));
    }


}
