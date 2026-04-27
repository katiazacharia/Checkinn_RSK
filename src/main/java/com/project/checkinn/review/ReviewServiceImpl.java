package com.project.checkinn.review;

import com.project.checkinn.booking.reservation.Booking;
import com.project.checkinn.security.CurrentUserService;
import com.project.checkinn.user.profile.User;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepo reviewRepository;
    private final EntityManager entityManager;
    private final CurrentUserService currentUserService;

    public ReviewServiceImpl(ReviewRepo reviewRepository, EntityManager entityManager, CurrentUserService currentUserService) {
        this.reviewRepository = reviewRepository;
        this.entityManager = entityManager;
        this.currentUserService = currentUserService;
    }

    @Override
    public Review create(ReviewRequest request, Authentication authentication) {
        if (request == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request is required");
        if (request.getBookingId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bookingId is required");
        if (request.getRating() < 1 || request.getRating() > 5)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "rating must be between 1 and 5");

        Long currentUserId = currentUserService.getCurrentUserId(authentication);
        if (reviewRepository.existsByUser_IdAndBooking_Id(currentUserId, request.getBookingId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Review already exists for this booking");
        }

        User user = entityManager.find(User.class, currentUserId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        Booking booking = entityManager.find(Booking.class, request.getBookingId());
        if (booking == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found");
        }
        Review review = ReviewMapper.toEntity(request, user, booking);
        review.setCreatedAt(LocalDateTime.now());
        return reviewRepository.save(review);
    }

    @Override
    public Review update(Long id, ReviewRequest request) {
        Review existing = getById(id);
        existing.setRating(request.getRating());
        existing.setComment(request.getComment());
        return reviewRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        Review existing = getById(id);
        reviewRepository.delete(existing);
    }

        @Override
    public Review getById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));
    }

    @Override
    public Page<Review> search(
            Long userId,
            Long bookingId,
            Integer rating,
            Integer minRating,
            Integer maxRating,
            Boolean hasComment,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable,
            Authentication authentication

    ) {
        if (minRating != null && (minRating < 1 || minRating > 5))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "minRating must be between 1 and 5");
        if (maxRating != null && (maxRating < 1 || maxRating > 5))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "maxRating must be between 1 and 5");
        if (minRating != null && maxRating != null && minRating > maxRating)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "minRating cannot be greater than maxRating");

        Specification<Review> spec = Specification.where(ReviewSpec.userId(userId))
                .and(ReviewSpec.bookingId(bookingId))
                .and(ReviewSpec.rating(rating))
                .and(ReviewSpec.ratingBetween(minRating, maxRating))
                .and(ReviewSpec.hasComment(hasComment))
                .and(ReviewSpec.createdFrom(from))
                .and(ReviewSpec.createdTo(to));

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean isManager = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"));

        if (isManager && !isAdmin) {
            Long currentUserId = currentUserService.getCurrentUserId(authentication);
            spec = spec.and(ReviewSpec.managerId(currentUserId));
        }
        return reviewRepository.findAll(spec, pageable);
    }


    @Override
    public List<Review> getAll() {
        return reviewRepository.findAll();
    }

    @Override
    public List<Review> getByUser(Long userId, Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean isManager = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"));

        Specification<Review> spec = Specification.where(ReviewSpec.userId(userId));

        if (isManager && !isAdmin) {
            Long currentUserId = currentUserService.getCurrentUserId(authentication);
            spec = spec.and(ReviewSpec.managerId(currentUserId));
        }
        return reviewRepository.findAll(spec);
    }

    @Override
    public List<Review> getByBooking(Long bookingId, Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean isManager = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"));

        Specification<Review> spec = Specification.where(ReviewSpec.bookingId(bookingId));

        if (isManager && !isAdmin) {
            Long currentUserId = currentUserService.getCurrentUserId(authentication);
            spec = spec.and(ReviewSpec.managerId(currentUserId));
        }
        return reviewRepository.findAll(spec);
    }
}