package com.example.cscb07_final_project_smartair.DataObjects;

import com.google.firebase.auth.FirebaseAuth;

public class RescueDose extends Data {
    public int doseAmount;
    public int breathingBefore;
    public int breathingAfter;
    public int shortnessOfBreath;

    public RescueDose() {}

    public RescueDose(int doseAmount, int breathingBefore, int breathingAfter, int shortnessOfBreath) {
        super(System.currentTimeMillis(), FirebaseAuth.getInstance().getCurrentUser().getUid(), "rescueDose");
        this.doseAmount = doseAmount;
        this.breathingBefore = breathingBefore;
        this.breathingAfter = breathingAfter;
        this.shortnessOfBreath = shortnessOfBreath;
    }
}
