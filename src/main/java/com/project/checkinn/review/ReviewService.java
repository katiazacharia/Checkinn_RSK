package com.project.checkinn.review;

import java.util.List;

public interface ReviewService {

    Review create(ReviewRequest request);
    Review getById(Long id);
    List<Review> getAll();
    List<Review> getByUser(Long userId);
    List<Review> getByBooking(Long bookingId);


}