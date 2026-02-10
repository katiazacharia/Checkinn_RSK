package com.project.checkinn.review;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // GET /reviews
    @GetMapping
    public List<ReviewResponse> all() {
        return reviewService.getAll()
                .stream()
                .map(ReviewMapper::toResponse)
                .toList();
    }

    // GET /reviews/{id}
    @GetMapping("/{id}")
    public ReviewResponse one(@PathVariable Long id) {
        return ReviewMapper.toResponse(reviewService.getById(id));
    }

    // POST /reviews -> 201 Created + Location
    @PostMapping
    public ResponseEntity<ReviewResponse> create(
            @RequestBody ReviewRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        Review saved = reviewService.create(request);
        ReviewResponse response = ReviewMapper.toResponse(saved);

        URI location = uriBuilder
                .path("/reviews/{id}")
                .buildAndExpand(saved.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    // GET /reviews/user/{userId}
    @GetMapping("/user/{userId}")
    public List<ReviewResponse> byUser(@PathVariable Long userId) {
        return reviewService.getByUser(userId)
                .stream()
                .map(ReviewMapper::toResponse)
                .toList();
    }

    // GET /reviews/booking/{bookingId}
    @GetMapping("/booking/{bookingId}")
    public List<ReviewResponse> byBooking(@PathVariable Long bookingId) {
        return reviewService.getByBooking(bookingId)
                .stream()
                .map(ReviewMapper::toResponse)
                .toList();
    }
}