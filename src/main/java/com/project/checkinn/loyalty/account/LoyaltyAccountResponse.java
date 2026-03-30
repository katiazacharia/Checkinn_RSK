package com.project.checkinn.loyalty.account;

import com.project.checkinn.common.Tier;

import java.time.LocalDateTime;

public class LoyaltyAccountResponse {

    private Long id;
    private Long userId;
    private int points;
    private Tier tier;
    private LocalDateTime updatedAt;

    public LoyaltyAccountResponse(Long id, Long userId, int points, LocalDateTime updatedAt, Tier tier) {
        this.id = id;
        this.userId = userId;
        this.points = points;
        this.updatedAt = updatedAt;
        this.tier = tier;
    }

    public LoyaltyAccountResponse(LoyaltyAccount acc) {
        this.id = acc.getId();
        this.userId = acc.getUser().getId();
        this.points = acc.getPoints();
        this.updatedAt = acc.getUpdatedAt();
        this.tier = acc.getTier();
    }

    public Long getId() {
        return id; }
    public Long getUserId() {
        return userId; }
    public int getPoints() {
        return points; }
    public Tier getTier() { return tier; }
    public LocalDateTime getUpdatedAt() {
        return updatedAt; }
}
