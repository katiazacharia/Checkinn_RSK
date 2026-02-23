package com.project.checkinn.notification;

import com.project.checkinn.common.NotificationStatus;
import com.project.checkinn.common.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationService {

    Notification create(
            Long userId,
            Long bookingId,
            NotificationType type,
            String title,
            String message
    );

    Page<Notification> search(
            Long userId,
            Long bookingId,
            NotificationType type,
            NotificationStatus status,
            LocalDateTime from,
            LocalDateTime to,
            String q,
            Pageable pageable
    );


    Notification createFromRequest(NotificationRequest request);

    Notification getById(Long id);

    List<Notification> getAll();

    List<Notification> getByUser(Long userId);

    List<Notification> getByUserAndStatus(Long userId, NotificationStatus status);

    Notification updateStatus(Long id, NotificationStatus status);

    Notification markRead(Long id);

    void markReadAll(Long userId);

    void delete(Long id);
}
