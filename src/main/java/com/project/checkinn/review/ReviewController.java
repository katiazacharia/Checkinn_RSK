package com.project.checkinn.review;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }


    @GetMapping
    public Page<ReviewResponse> search(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long bookingId,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) Integer minRating,
            @RequestParam(required = false) Integer maxRating,
            @RequestParam(required = false) Boolean hasComment,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable
    ) {
        return reviewService.search(userId, bookingId, rating, minRating, maxRating, hasComment, from, to, pageable)
                .map(ReviewMapper::toResponse);
    }

    // GET /reviews/{id}
    @GetMapping("/{id}")
    public ReviewResponse one(@PathVariable Long id) {
        return ReviewMapper.toResponse(reviewService.getById(id));
    }

    // POST /reviews
    @PostMapping
    public ResponseEntity<ReviewResponse> create(
          @Valid @RequestBody ReviewRequest request,
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

    // PUT /reviews/{id}
    @PutMapping("/{id}")
    public ReviewResponse update(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequest request
    ) {
        return ReviewMapper.toResponse(reviewService.update(id, request));
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
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reviewService.delete(id);
        return ResponseEntity.noContent().build();
    }
}