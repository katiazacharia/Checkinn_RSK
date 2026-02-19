package com.project.checkinn.notification;

import com.project.checkinn.common.NotificationStatus;
import com.project.checkinn.common.NotificationType;
import java.util.List;

public interface NotificationService {

    Notification create(
            Long userId,
            Long bookingId,
            NotificationType type,
            String title,
            String message
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