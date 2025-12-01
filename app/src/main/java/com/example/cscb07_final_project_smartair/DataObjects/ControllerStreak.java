package com.example.cscb07_final_project_smartair.DataObjects;

import com.google.firebase.auth.FirebaseAuth;

public class ControllerStreak extends Data {
    public int streakCount;
    public long lastUpdated;

    public ControllerStreak() {}

    public ControllerStreak(int streakCount, long lastUpdated) {
        super(System.currentTimeMillis(), FirebaseAuth.getInstance().getCurrentUser().getUid(), "controllerStreak");
        this.streakCount = streakCount;
        this.lastUpdated = lastUpdated;
    }
}
