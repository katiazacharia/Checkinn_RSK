package com.project.checkinn.review;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepo extends JpaRepository<Review, Long> {
    List<Review> findByUser_Id(Long userId);
    List<Review> findByBooking_Id(Long bookingId);
    boolean existsByUser_IdAndBooking_Id(Long userId, Long bookingId);
}