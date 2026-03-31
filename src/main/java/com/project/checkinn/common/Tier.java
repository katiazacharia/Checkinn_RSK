package com.project.checkinn.common;

public enum Tier {
    BRONZE(0.05, 0.10,0,299 ),
    SILVER(0.07, 0.215,300 , 799),
    GOLD(0.10, 0.20,800 ,Integer.MAX_VALUE );

    private final double pointValue;
    private final double maxDiscount;
    private final int minPoints;
    private final int maxPoints;

    Tier(double pointValue, double maxDiscount, int minPoints, int maxPoints) {
        this.pointValue = pointValue;
        this.maxDiscount = maxDiscount;
        this.minPoints = minPoints;
        this.maxPoints = maxPoints;
    }

    public double getPointValue() {
        return pointValue;
    }

    public double getMaxDiscount() {
        return maxDiscount;
    }

    public int getMinPoints()      { return minPoints; }

    public int getMaxPoints()      { return maxPoints; }

    public static Tier fromPoints(int points) {
        if (points >= 800)  return GOLD;
        if (points >= 300)  return SILVER;
        return BRONZE;
    }
}
