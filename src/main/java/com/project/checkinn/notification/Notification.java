package com.project.checkinn.notification;

import jakarta.persistence.*;

@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String message;
    private boolean readStatus;

    public Notification() {}

    public Notification(String title, String message) {
        this.title = title;
        this.message = message;
        this.readStatus = false;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public boolean isReadStatus() { return readStatus; }

    public void setTitle(String title) { this.title = title; }
    public void setMessage(String message) { this.message = message; }
    public void setReadStatus(boolean readStatus) { this.readStatus = readStatus; }
}
