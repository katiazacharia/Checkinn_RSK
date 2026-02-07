package com.project.checkinn.review;

import java.time.LocalDateTime;

public class ReviewResponse {

    private Long id;
    private Long userId;
    private Long bookingId;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;

    public ReviewResponse(Review review) {
        this.id = review.getId();

        this.userId = review.getUser() != null ? review.getUser().getId() : null;

        this.bookingId = review.getBooking() != null ? review.getBooking().getId() : null;

        this.rating = review.getRating();

        this.comment = review.getComment();

        this.createdAt = review.getCreatedAt();
    }

    public Long getId() {
        return id;


    }

    public Long getUserId() {
        return userId;

    }


    public Long getBookingId() {
        return bookingId;

    }

    public int getRating() {
        return rating;

    }

    public String getComment() {
        return comment;

    }

    public LocalDateTime getCreatedAt() {
        return createdAt;


    }
}