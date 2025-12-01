package com.example.cscb07_final_project_smartair.Models;

import androidx.annotation.NonNull;

import com.example.cscb07_final_project_smartair.DataObjects.ControllerDose;
import com.example.cscb07_final_project_smartair.DataObjects.InventoryItem;
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

    public void logController(int doseAmount, int breathingBefore, int breathingAfter) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            presenter.onFailure("User not logged in. Restart the app.");
            return;
        }

        String childID = user.getUid();
        ControllerDose log = new ControllerDose(doseAmount, breathingBefore, breathingAfter);

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child("parents")
                .child(childID)
                .child("children")
                .child(childID)
                .child("medicine")
                .child("controller");

        String logID = ref.push().getKey();
        if (logID == null) {
            presenter.onFailure("Failed to generate log ID.");
            return;
        }

        ref.child(logID).setValue(log)
                .addOnSuccessListener(aVoid -> {
                    reduceInventory(childID, "controller", doseAmount);
                    presenter.onControllerLogSuccess();
                })
                .addOnFailureListener(e -> presenter.onFailure(e.getMessage()));
    }

    public void logRescue(int doseAmount, int breathingBefore, int breathingAfter, int shortnessOfBreath) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            presenter.onFailure("User not logged in. Restart the app.");
            return;
        }

        String childID = user.getUid();
        RescueDose log = new RescueDose(doseAmount, breathingBefore, breathingAfter, shortnessOfBreath);

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child("parents")
                .child(childID)
                .child("children")
                .child(childID)
                .child("medicine")
                .child("rescue");

        String logID = ref.push().getKey();
        if (logID == null) {
            presenter.onFailure("Failed to generate log ID.");
            return;
        }

        ref.child(logID).setValue(log)
                .addOnSuccessListener(aVoid -> {
                    reduceInventory(childID, "rescue", doseAmount);
                    presenter.onRescueLogSuccess();
                })
                .addOnFailureListener(e -> presenter.onFailure(e.getMessage()));
    }

    public void getControllerDoses() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            presenter.onFailure("User not logged in.");
            return;
        }

        String childID = user.getUid();
        long cutoff = System.currentTimeMillis() - (72L * 60L * 60L * 1000);

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child("parents")
                .child(childID)
                .child("children")
                .child(childID)
                .child("medicine")
                .child("controller");

        ref.orderByChild("timestamp").startAt(cutoff)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<ControllerDose> list = new ArrayList<>();

                        for (DataSnapshot ds : snapshot.getChildren())
                        {
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

    public void getRescueDoses() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            presenter.onFailure("User not logged in.");
            return;
        }

        String childID = user.getUid();
        long cutoff = System.currentTimeMillis() - (72L * 60L * 60L * 1000);

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child("parents")
                .child(childID)
                .child("children")
                .child(childID)
                .child("medicine")
                .child("rescue");

        ref.orderByChild("timestamp").startAt(cutoff)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<RescueDose> list = new ArrayList<>();

                        for (DataSnapshot ds : snapshot.getChildren())
                        {
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

    private void reduceInventory(String childId, String medType, int amountToReduce) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String parentID = user.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child("parents")
                .child(parentID)
                .child("children")
                .child(childId)
                .child("inventory");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                InventoryItem target = null;
                String key = null;

                for (DataSnapshot ds : snapshot.getChildren()) {
                    InventoryItem item = ds.getValue(InventoryItem.class);
                    if (item == null) continue;

                    if (medType.equals(item.medType)) {
                        target = item;
                        key = ds.getKey();
                        break;
                    }
                }

                if (target == null || key == null) return;

                target.amountLeft = Math.max(0, target.amountLeft - amountToReduce);
                ref.child(key).setValue(target);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}
