package com.project.checkinn.review;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
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
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/{id}")
    public ReviewResponse one(@PathVariable Long id) {
        return ReviewMapper.toResponse(reviewService.getById(id));
    }

    // POST /reviews
//    @PreAuthorize("(hasRole('CUSTOMER') and @authz.isUserOwner(#request.userId, authentication)) or hasAnyRole('ADMIN','MANAGER')")
    @PreAuthorize("hasRole('CUSTOMER') or hasAnyRole('ADMIN','MANAGER')")
    @PostMapping
    public ResponseEntity<ReviewResponse> create(
          @Valid @RequestBody ReviewRequest request,
            UriComponentsBuilder uriBuilder,
        Authentication authentication
    ) {
        Review saved = reviewService.create(request,authentication);
        ReviewResponse response = ReviewMapper.toResponse(saved);

        URI location = uriBuilder
                .path("/reviews/{id}")
                .buildAndExpand(saved.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    // PUT /reviews/{id}
    @PreAuthorize("@authz.isReviewOwner(#id, authentication) or hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/{id}")
    public ReviewResponse update(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequest request
    ) {
        return ReviewMapper.toResponse(reviewService.update(id, request));
    }

    // GET /reviews/user/{userId}
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/user/{userId}")
    public List<ReviewResponse> byUser(@PathVariable Long userId) {
        return reviewService.getByUser(userId)
                .stream()
                .map(ReviewMapper::toResponse)
                .toList();
    }

    // GET /reviews/booking/{bookingId}
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/booking/{bookingId}")
    public List<ReviewResponse> byBooking(@PathVariable Long bookingId) {
        return reviewService.getByBooking(bookingId)
                .stream()
                .map(ReviewMapper::toResponse)
                .toList();
    }

    @PreAuthorize("@authz.isReviewOwner(#id, authentication) or hasAnyRole('ADMIN','MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reviewService.delete(id);
        return ResponseEntity.noContent().build();
    }
}