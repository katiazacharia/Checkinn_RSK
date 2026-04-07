package com.project.checkinn.review;

import com.project.checkinn.booking.reservation.Booking;
import com.project.checkinn.security.CurrentUserService;
import com.project.checkinn.user.profile.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock ReviewRepo reviewRepository;
    @Mock EntityManager entityManager;
    @Mock CurrentUserService currentUserService;
    @Mock Authentication authentication;

    @InjectMocks ReviewServiceImpl service;

    private ReviewRequest request;

    @BeforeEach
    void setUp() {
        request = new ReviewRequest();
        request.setBookingId(1L);
        request.setRating(5);
        request.setComment("Great stay");
    }

    @Test
    void create_shouldSaveReview_whenValid() {
        User user = new User();
        Booking booking = new Booking();
        when(currentUserService.getCurrentUserId(authentication)).thenReturn(2L);
        when(reviewRepository.existsByUser_IdAndBooking_Id(2L, 1L)).thenReturn(false);
        when(entityManager.find(User.class, 2L)).thenReturn(user);
        when(entityManager.find(Booking.class, 1L)).thenReturn(booking);
        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> inv.getArgument(0));

        Review saved = service.create(request, authentication);

        assertEquals(5, saved.getRating());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void create_shouldThrowConflict_whenDuplicateReview() {
        when(currentUserService.getCurrentUserId(authentication)).thenReturn(2L);
        when(reviewRepository.existsByUser_IdAndBooking_Id(2L, 1L)).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> service.create(request, authentication));
    }

    @Test
    void update_shouldModifyExistingReview() {
        Review existing = new Review();
        existing.setRating(3);
        when(reviewRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(reviewRepository.save(existing)).thenReturn(existing);

        request.setRating(4);
        request.setComment("Updated");
        Review updated = service.update(7L, request);

        assertEquals(4, updated.getRating());
        assertEquals("Updated", updated.getComment());
    }
}
