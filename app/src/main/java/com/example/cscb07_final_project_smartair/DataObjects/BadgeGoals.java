package com.example.cscb07_final_project_smartair.DataObjects;

import com.google.firebase.auth.FirebaseAuth;

public class BadgeGoals extends Data {

    public int perfectControllerWeekDays;
    public int highQualityTechniqueSessions;
    public int lowRescueMonthLimit;

    public BadgeGoals() {}

    public BadgeGoals(int perfectControllerWeekDays, int highQualityTechniqueSessions, int lowRescueMonthLimit) {
        super(System.currentTimeMillis(), FirebaseAuth.getInstance().getCurrentUser().getUid(), "badgeGoals");
        this.perfectControllerWeekDays = perfectControllerWeekDays;
        this.highQualityTechniqueSessions = highQualityTechniqueSessions;
        this.lowRescueMonthLimit = lowRescueMonthLimit;
    }
}
