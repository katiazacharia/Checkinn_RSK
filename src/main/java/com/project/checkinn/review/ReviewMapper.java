package com.project.checkinn.review;

import com.project.checkinn.booking.reservation.Booking;
import com.project.checkinn.user.profile.User;

import java.time.LocalDateTime;

public class ReviewMapper {

    private ReviewMapper() {}

    public static ReviewResponse toResponse(Review review) {
        return review == null ? null : new ReviewResponse(review);
    }

    public static Review toEntity(ReviewRequest request, User user, Booking booking) {
        if (request == null || user == null || booking == null) return null;

        Review review = new Review();
        review.setUser(user);
        review.setBooking(booking);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setCreatedAt(LocalDateTime.now());

        return review;
    }
}