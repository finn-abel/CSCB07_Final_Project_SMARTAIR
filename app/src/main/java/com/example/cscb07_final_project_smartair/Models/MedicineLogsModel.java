package com.example.cscb07_final_project_smartair.Models;

import androidx.annotation.NonNull;

import com.example.cscb07_final_project_smartair.DataObjects.ControllerDose;
import com.example.cscb07_final_project_smartair.DataObjects.RescueDose;
import com.example.cscb07_final_project_smartair.Presenters.MedicineLogsPresenter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class MedicineLogsModel {
    private final MedicineLogsPresenter presenter;

    public MedicineLogsModel(MedicineLogsPresenter presenter) {
        this.presenter = presenter;
    }

    // adds controller dose to db
    public void logController(int doseAmount) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            presenter.onFailure("User not logged in. Restart the app.");
            return;
        }

        String childID = user.getUid();
        ControllerDose log = new ControllerDose(doseAmount);

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("medicine")
                .child("controller")
                .child(childID);
        String logID = ref.push().getKey();

        if (logID == null) {
            presenter.onFailure("Failed to generate log ID.");
            return;
        }

        ref.child(logID).setValue(log)
                .addOnSuccessListener(aVoid -> presenter.onControllerLogSuccess())
                .addOnFailureListener(e -> presenter.onFailure(e.getMessage()));
    }

    // adds Rescue dose to db
    public void logRescue(int doseAmount, int breathingBefore, int breathingAfter, int shortnessOfBreath) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            presenter.onFailure("User not logged in. Restart the app.");
            return;
        }

        String childID = user.getUid();
        RescueDose log = new RescueDose(doseAmount, breathingBefore, breathingAfter, shortnessOfBreath);

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("medicine")
                .child("rescue")
                .child(childID);
        String logID = ref.push().getKey();

        if (logID == null) {
            presenter.onFailure("Failed to generate log ID.");
            return;
        }

        ref.child(logID).setValue(log)
                .addOnSuccessListener(aVoid -> presenter.onRescueLogSuccess())
                .addOnFailureListener(e -> presenter.onFailure(e.getMessage()));
    }

    //gets Controller doses for last 72h
    public void getControllerDoses() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            presenter.onFailure("User not logged in.");
            return;
        }

        String childID = user.getUid();
        long cutoff = System.currentTimeMillis() - (72L * 60L * 60L * 1000L);

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("medicine")
                .child("controller")
                .child(childID);

        ref.orderByChild("timestamp").startAt(cutoff)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<ControllerDose> list = new ArrayList<>();

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ControllerDose log = ds.getValue(ControllerDose.class);
                            if (log != null) list.add(log);
                        }

                        presenter.onControllerLogsLoaded(list);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        presenter.onFailure(error.getMessage());
                    }
                });
    }
    //gets Rescue doses for last 72h
    public void getRescueDoses() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            presenter.onFailure("User not logged in.");
            return;
        }

        String childID = user.getUid();
        long cutoff = System.currentTimeMillis() - (72L * 60L * 60L * 1000L);

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("medicine")
                .child("rescue")
                .child(childID);

        ref.orderByChild("timestamp").startAt(cutoff)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<RescueDose> list = new ArrayList<>();

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            RescueDose log = ds.getValue(RescueDose.class);
                            if (log != null) list.add(log);
                        }

                        presenter.onRescueLogsLoaded(list);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        presenter.onFailure(error.getMessage());
                    }
                });
    }
}
