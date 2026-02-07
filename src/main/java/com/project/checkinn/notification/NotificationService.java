package com.project.checkinn.notification;

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

    Notification getById(Long id);

    List<Notification> getAll();
}