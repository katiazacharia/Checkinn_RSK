package com.project.checkinn.loyalty.account;

import java.time.LocalDateTime;

public class LoyaltyAccountResponse {

    private Long id;
    private Long userId;
    private int points;
    private LocalDateTime updatedAt;

    public LoyaltyAccountResponse(LoyaltyAccount acc) {
        this.id = acc.getId();
        this.userId = acc.getUser().getId();
        this.points = acc.getPoints();
        this.updatedAt = acc.getUpdatedAt();
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public int getPoints() { return points; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}