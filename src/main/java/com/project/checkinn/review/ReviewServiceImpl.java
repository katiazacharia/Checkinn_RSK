package com.project.checkinn.review;

import com.project.checkinn.booking.reservation.Booking;
import com.project.checkinn.user.profile.User;
import jakarta.persistence.EntityManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepo reviewRepository;
    private final EntityManager entityManager;

    public ReviewServiceImpl(ReviewRepo reviewRepository, EntityManager entityManager) {
        this.reviewRepository = reviewRepository;
        this.entityManager = entityManager;
    }

    @Override
    public Review create(ReviewRequest request) {
        if (request == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request is required");
        if (request.getUserId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        if (request.getBookingId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bookingId is required");
        if (request.getRating() < 1 || request.getRating() > 5)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "rating must be between 1 and 5");

        if (reviewRepository.existsByUser_IdAndBooking_Id(request.getUserId(), request.getBookingId()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Review already exists for this booking");

        User userRef = entityManager.getReference(User.class, request.getUserId());
        Booking bookingRef = entityManager.getReference(Booking.class, request.getBookingId());

        Review review = ReviewMapper.toEntity(request, userRef, bookingRef);
        return reviewRepository.save(review);
    }

    @Override
    public Review getById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));
    }

    @Override
    public List<Review> getAll() {
        return reviewRepository.findAll();
    }

    @Override
    public List<Review> getByUser(Long userId) {
        return reviewRepository.findByUser_Id(userId);
    }

    @Override
    public List<Review> getByBooking(Long bookingId) {
        return reviewRepository.findByBooking_Id(bookingId);
    }
}