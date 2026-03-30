package com.project.checkinn.common;

public enum Tier {
    BRONZE(0.05, 0.15),
    SILVER(0.07, 0.20),
    GOLD(0.10, 0.30);

    private final double pointValue;
    private final double maxDiscount;

    Tier(double pointValue, double maxDiscount) {
        this.pointValue = pointValue;
        this.maxDiscount = maxDiscount;
    }

    public double getPointValue() {
        return pointValue;
    }

    public double getMaxDiscount() {
        return maxDiscount;
    }
}
