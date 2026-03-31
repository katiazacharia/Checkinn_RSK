package com.project.checkinn.notification;

import com.project.checkinn.common.NotificationStatus;
import com.project.checkinn.common.NotificationType;

import java.time.LocalDateTime;

public class NotificationResponse {

    private Long id;
    private Long userId;
    private Long bookingId;
    private NotificationType type;
    private String title;
    private String message;
    private NotificationStatus status;
    private LocalDateTime sentAt;

    public NotificationResponse(Notification n) {
        this.id = n.getId();
        this.userId = n.getUser() != null ? n.getUser().getId() : null;
        this.bookingId = n.getBooking() != null ? n.getBooking().getId() : null;
        this.type = n.getType();
        this.title = n.getTitle();
        this.message = n.getMessage();
        this.status = n.getStatus();
        this.sentAt = n.getSentAt();
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getBookingId() { return bookingId; }
    public NotificationType getType() { return type; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public NotificationStatus getStatus() { return status; }
    public LocalDateTime getSentAt() { return sentAt; }
}

