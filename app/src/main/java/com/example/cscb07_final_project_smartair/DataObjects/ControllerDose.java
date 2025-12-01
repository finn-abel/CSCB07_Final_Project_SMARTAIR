package com.example.cscb07_final_project_smartair.DataObjects;

import com.google.firebase.auth.FirebaseAuth;

public class ControllerDose extends Data {
    public int doseAmount;
    public int breathingBefore;
    public int breathingAfter;

    public ControllerDose() {
    }

    public ControllerDose(int doseAmount) {
        super(System.currentTimeMillis(), FirebaseAuth.getInstance().getCurrentUser().getUid(), "controllerDose");
        this.doseAmount = doseAmount;
        this.breathingBefore = 1;
        this.breathingAfter = 1;
    }
}
