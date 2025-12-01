package com.example.cscb07_final_project_smartair.DataObjects;

import com.google.firebase.auth.FirebaseAuth;

public class ControllerDose extends Data {
    public int doseAmount;
    public int breathingBefore;
    public int breathingAfter;

    public ControllerDose() {
    }

    public ControllerDose(int doseAmount, int before, int after) {
        super(System.currentTimeMillis(), FirebaseAuth.getInstance().getCurrentUser().getUid(), "controllerDose");
        this.doseAmount = doseAmount;
        this.breathingBefore = before;
        this.breathingAfter = after;
    }
}
