package com.example.cscb07_final_project_smartair.DataObjects;

public class BadgeThresholds {
    public int perfectWeekGoal;
    public int techniqueGoal;
    public int lowRescueGoal;

    public BadgeThresholds() {
    }

    public BadgeThresholds(int perfectWeekGoal, int techniqueGoal, int lowRescueGoal) {
        this.perfectWeekGoal = perfectWeekGoal;
        this.techniqueGoal = techniqueGoal;
        this.lowRescueGoal = lowRescueGoal;
    }

    public static BadgeThresholds getDefault() {
        return new BadgeThresholds(7, 10, 4);
    }
}
