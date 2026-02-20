package com.project.checkinn.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewService {

    Review create(ReviewRequest request);
    Review update(Long id, ReviewRequest request);
    void delete(Long id);
    Review getById(Long id);

    Page<Review> search(
            Long userId,
            Long bookingId,
            Integer rating,
            Integer minRating,
            Integer maxRating,
            Boolean hasComment,
            java.time.LocalDateTime from,
            java.time.LocalDateTime to,
            Pageable pageable
    );


    List<Review> getAll();
    List<Review> getByUser(Long userId);
    List<Review> getByBooking(Long bookingId);


}