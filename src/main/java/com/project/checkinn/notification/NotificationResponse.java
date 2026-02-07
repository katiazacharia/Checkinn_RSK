package com.project.checkinn.notification;

import java.time.Instant;

public class NotificationResponse {

    private Long id;
    private String title;
    private String message;
    private String status;
    private Instant createdAt;

    public NotificationResponse() {}

    public NotificationResponse(Long id, String title, String message, String status, Instant createdAt) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
