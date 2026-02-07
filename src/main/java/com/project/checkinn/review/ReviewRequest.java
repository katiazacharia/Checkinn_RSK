package com.project.checkinn.review;

public class ReviewRequest {
    private Long userId;
    private Long bookingId;
    private int rating;
    private String comment;

    public Long getUserId() {

        return userId;

    }


    public void setUserId(Long userId) {

        this.userId = userId;

    }

    public Long getBookingId() {

        return bookingId;

    }
    public void setBookingId(Long bookingId) {

        this.bookingId = bookingId;

    }

    public int getRating() {

        return rating;

    }


    public void setRating(int rating) {

        this.rating = rating;

    }

    public String getComment() {

        return comment;

    }

    public void setComment(String comment) {

        this.comment = comment;

    }
}