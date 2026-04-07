package com.project.checkinn.notification;

import com.project.checkinn.booking.reservation.Booking;
import com.project.checkinn.booking.reservation.BookingRepository;
import com.project.checkinn.common.NotificationStatus;
import com.project.checkinn.common.NotificationType;
import com.project.checkinn.security.CurrentUserService;
import com.project.checkinn.user.profile.User;
import com.project.checkinn.user.profile.UserRepo;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock NotificationRepository notificationRepository;
    @Mock EntityManager entityManager;
    @Mock UserRepo userRepository;
    @Mock BookingRepository bookingRepository;
    @Mock CurrentUserService currentUserService;
    @Mock Authentication authentication;

    @InjectMocks NotificationServiceImpl service;

    @Test
    void create_shouldSaveUnreadNotification() {
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

        Notification n = service.create(1L, null, NotificationType.EMAIL, "Title", "Body");

        assertEquals(NotificationStatus.UNREAD, n.getStatus());
        assertEquals(NotificationType.EMAIL, n.getType());
    }

    @Test
    void create_shouldThrow_whenUserMissing() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class,
                () -> service.create(1L, null, NotificationType.EMAIL, "Title", "Body"));
    }

    @Test
    void markRead_shouldSetSent_whenUnread() {
        Notification n = new Notification();
        n.setStatus(NotificationStatus.UNREAD);
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(n));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

        Notification saved = service.markRead(1L);

        assertEquals(NotificationStatus.SENT, saved.getStatus());
    }

    @Test
    void markAllMyRead_shouldUseCurrentUserId() {
        when(currentUserService.getCurrentUserId(authentication)).thenReturn(5L);
        Notification n = new Notification();
        n.setStatus(NotificationStatus.UNREAD);
        when(notificationRepository.findByUser_IdAndStatusOrderBySentAtDesc(5L, NotificationStatus.UNREAD))
                .thenReturn(List.of(n));

        service.markAllMyRead(authentication);

        verify(notificationRepository).saveAll(anyList());
    }
}
