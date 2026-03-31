package com.project.checkinn.booking.reservation;


import com.project.checkinn.booking.preview.BookingPreviewRequest;
import com.project.checkinn.booking.preview.BookingPreviewResponse;
import com.project.checkinn.common.BookingStatus;
import com.project.checkinn.security.CurrentUserService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService ) {
        this.bookingService = bookingService;
    }

//list with optional filters: status, userId, roomId, from, to
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<BookingResponse> all(@RequestParam(required = false) BookingStatus status,
                                     @RequestParam(required = false) Long userId,
                                     @RequestParam(required = false) Long roomId,
                                     @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                     @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return bookingService.search(status, userId, roomId, from, to)
                .stream()
                .map(BookingMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("@authz.isBookingOwner(#id, authentication) or hasAnyRole('ADMIN','MANAGER')")
    public BookingResponse one(@PathVariable Long id) {

        return BookingMapper.toResponse(bookingService.getById(id));
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<BookingResponse> byUser(@PathVariable Long userId) {
        return bookingService.getByUser(userId)
                .stream()
                .map(BookingMapper::toResponse)
                .toList();
    }
//bashof al upcoming l specific user
    @GetMapping("/upcoming")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<BookingResponse> upcoming(
            @RequestParam(required = false) Long userId
    ) {
        return bookingService.upcoming(userId)
                .stream()
                .map(BookingMapper::toResponse)
                .toList();
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BookingResponse> create(
           @Valid @RequestBody BookingRequest request,
            UriComponentsBuilder uriBuilder, Authentication authentication

    ) {
        Booking saved = bookingService.createMyBooking(request,authentication);
        BookingResponse response = BookingMapper.toResponse(saved);

        URI location = uriBuilder
                .path("/bookings/{id}")
                .buildAndExpand(saved.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("(hasRole('CUSTOMER') and @authz.isBookingOwner(#id, authentication)) or hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<BookingResponse> cancel(@PathVariable Long id) {
        Booking cancelled = bookingService.cancel(id);
        return ResponseEntity.ok(BookingMapper.toResponse(cancelled));
    }

    @PostMapping("/preview")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BookingPreviewResponse> preview(
            @Valid @RequestBody BookingPreviewRequest request
    ) {
        return ResponseEntity.ok(bookingService.preview(request));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<BookingResponse> myBookings(Authentication authentication) {

        return bookingService.getMyBookings(authentication)
                .stream()
                .map(BookingMapper::toResponse)
                .toList();
    }



}
