package com.project.checkinn.loyalty.transaction;

import com.project.checkinn.common.LoyaltyTransactionType;

import java.time.LocalDateTime;

public class LoyaltyTransactionResponse {

    private Long id;
    private Long userId;
    private LoyaltyTransactionType type;
    private int points;
    private String note;
    private LocalDateTime createdAt;

    public LoyaltyTransactionResponse(LoyaltyTransaction tx) {
        this.id = tx.getId();
        this.userId = tx.getUser() != null ? tx.getUser().getId() : null;
        this.type = tx.getType();
        this.points = tx.getPoints();
        this.note = tx.getNote();
        this.createdAt = tx.getCreatedAt();
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public LoyaltyTransactionType getType() { return type; }
    public int getPoints() { return points; }
    public String getNote() { return note; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
