package com.project.checkinn.loyalty;

public class RedeemRequest {
    private int points;
    private String note;
    private double totalPrice;

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
}
