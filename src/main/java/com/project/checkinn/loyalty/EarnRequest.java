package com.project.checkinn.loyalty;

public class EarnRequest {
    private Long userId;
    private int points;
    private String note;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
