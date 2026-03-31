package com.project.checkinn.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReviewRequest {


    @NotNull(message = "bookingId is required")
    private Long bookingId;
    @Min(value = 1, message = "rating must be between 1 and 5")
    @Max(value = 5, message = "rating must be between 1 and 5")
    private int rating;
    @Size(max = 1000, message = "comment max length is 1000")
    private String comment;


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