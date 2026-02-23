package com.project.checkinn.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ReviewRepo extends JpaRepository<Review, Long> , JpaSpecificationExecutor<Review> {
    List<Review> findByUser_Id(Long userId);
    List<Review> findByBooking_Id(Long bookingId);
    boolean existsByUser_IdAndBooking_Id(Long userId, Long bookingId);
}