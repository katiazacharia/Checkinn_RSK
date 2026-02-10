package com.project.checkinn.notification;

import com.project.checkinn.common.NotificationStatus;
import com.project.checkinn.common.NotificationType;

public class NotificationRequest {

    private Long userId;
    private Long bookingId;
    private NotificationType type;
    private String title;
    private String message;
    private NotificationStatus status;

    public NotificationRequest() {}

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public NotificationStatus getStatus() { return status; }
    public void setStatus(NotificationStatus status) { this.status = status; }

}
