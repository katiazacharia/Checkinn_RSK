package com.project.checkinn.notification;

import com.project.checkinn.booking.reservation.Booking;
import com.project.checkinn.common.NotificationStatus;
import com.project.checkinn.common.NotificationType;
import com.project.checkinn.user.profile.User;

import java.time.LocalDateTime;

public class NotificationMapper {

    private NotificationMapper() {
        // utility class
    }

    public static NotificationResponse toResponse(Notification notification) {
        if (notification == null) return null;

        return new NotificationResponse(notification);
    }

    public static Notification toEntity(
            User user,
            Booking booking,
            NotificationType type,
            String title,
            String message
    ) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setBooking(booking);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        //notification.setStatus(NotificationStatus.UNREAD);
        notification.setSentAt(LocalDateTime.now());

        return notification;
    }
}