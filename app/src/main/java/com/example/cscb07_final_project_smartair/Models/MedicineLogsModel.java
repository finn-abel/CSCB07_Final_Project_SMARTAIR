package com.example.cscb07_final_project_smartair.Models;

import androidx.annotation.NonNull;

import com.example.cscb07_final_project_smartair.DataObjects.ControllerDose;
import com.example.cscb07_final_project_smartair.DataObjects.InventoryItem;
import com.example.cscb07_final_project_smartair.DataObjects.RescueDose;
import com.example.cscb07_final_project_smartair.Presenters.MedicineLogsPresenter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class MedicineLogsModel {
    private final MedicineLogsPresenter presenter;

    public MedicineLogsModel(MedicineLogsPresenter presenter) {
        this.presenter = presenter;
    }

    private String getChildId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return (user != null) ? user.getUid() : null;
    }

    private String getDayOfWeek() {
        return new SimpleDateFormat("EEEE", Locale.getDefault()).format(new Date());
    }

    public void logController(int doseAmount, int breathingBefore, int breathingAfter) {
        String childId = getChildId();
        if (childId == null) {
            presenter.onFailure("User not logged in.");
            return;
        }

        ControllerDose log = new ControllerDose(doseAmount, breathingBefore, breathingAfter);
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users/children/" + childId + "/medicine/controller");

        String logId = ref.push().getKey();
        if (logId == null) {
            presenter.onFailure("Failed to create log ID.");
            return;
        }

        ref.child(logId).setValue(log)
                .addOnSuccessListener(aVoid -> {
                    reduceInventory("controller", doseAmount);
                    updateControllerStreak(() -> {
                        evaluatePerfectControllerWeek();
                        presenter.onControllerLogSuccess();
                    });
                })
                .addOnFailureListener(e -> presenter.onFailure(e.getMessage()));
    }

    public void logRescue(int doseAmount, int breathingBefore, int breathingAfter, int shortnessOfBreath) {
        String childId = getChildId();
        if (childId == null) {
            presenter.onFailure("User not logged in.");
            return;
        }

        RescueDose log = new RescueDose(doseAmount, breathingBefore, breathingAfter, shortnessOfBreath);
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users/children/" + childId + "/medicine/rescue");

        String logId = ref.push().getKey();
        if (logId == null) {
            presenter.onFailure("Failed to create rescue log ID.");
            return;
        }

        ref.child(logId).setValue(log)
                .addOnSuccessListener(aVoid -> {
                    reduceInventory("rescue", doseAmount);
                    updateTechniqueStreak(() -> {
                        evaluateTechniqueMaster();
                        evaluateLowRescueMonth();
                        presenter.onRescueLogSuccess();
                    });
                })
                .addOnFailureListener(e -> presenter.onFailure(e.getMessage()));
    }

    public void getControllerDoses() {
        String childId = getChildId();
        if (childId == null) return;

        long cutoff = System.currentTimeMillis() - 72L * 60L * 60L * 1000;

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users/children/" + childId + "/medicine/controller");

        ref.orderByChild("timestamp").startAt(cutoff)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot ds) {
                        List<ControllerDose> list = new ArrayList<>();
                        for (DataSnapshot entry : ds.getChildren()) {
                            ControllerDose log = entry.getValue(ControllerDose.class);
                            if (log != null) list.add(log);
                        }
                        presenter.onControllerLogsLoaded(list);
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {
                        presenter.onFailure(error.getMessage());
                    }
                });
    }

    public void getRescueDoses() {
        String childId = getChildId();
        if (childId == null) return;

        long cutoff = System.currentTimeMillis() - 72L * 60L * 60L * 1000;

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users/children/" + childId + "/medicine/rescue");

        ref.orderByChild("timestamp").startAt(cutoff)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot ds) {
                        List<RescueDose> list = new ArrayList<>();
                        for (DataSnapshot entry : ds.getChildren()) {
                            RescueDose log = entry.getValue(RescueDose.class);
                            if (log != null) list.add(log);
                        }
                        presenter.onRescueLogsLoaded(list);
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {
                        presenter.onFailure(error.getMessage());
                    }
                });
    }

    private void reduceInventory(String medType, int amountToReduce) {

        String childId = getChildId();
        if (childId == null) return;

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users/children/" + childId + "/medicine/inventory");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snap) {

                for (DataSnapshot ds : snap.getChildren()) {
                    InventoryItem item = ds.getValue(InventoryItem.class);
                    if (item != null && medType.equals(item.medType)) {

                        item.amountLeft = Math.max(0, item.amountLeft - amountToReduce);
                        ref.child(ds.getKey()).setValue(item);
                        break;
                    }
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private interface Callback { void done(); }

    private void updateControllerStreak(Callback callback) {
        String childId = getChildId();
        if (childId == null) return;

        String today = getDayOfWeek();

        DatabaseReference scheduleRef = FirebaseDatabase.getInstance()
                .getReference("users/children/" + childId + "/medicine/schedule/" + today);

        scheduleRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot scheduleSnap) {
                if (!scheduleSnap.exists()) {
                    callback.done();
                    return;
                }

                DatabaseReference streakRef = FirebaseDatabase.getInstance()
                        .getReference("users/children/" + childId + "/medicine/motivation/streaks/controller");

                streakRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot s) {

                        int streak = s.child("streakCount").getValue(Integer.class) != null
                                ? s.child("streakCount").getValue(Integer.class) : 0;

                        streak++;

                        Map<String, Object> update = new HashMap<>();
                        update.put("streakCount", streak);
                        update.put("lastUpdated", System.currentTimeMillis());

                        streakRef.updateChildren(update).addOnSuccessListener(a -> callback.done());
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void updateTechniqueStreak(Callback callback) {
        String childId = getChildId();
        if (childId == null) return;

        DatabaseReference streakRef = FirebaseDatabase.getInstance()
                .getReference("users/children/" + childId + "/medicine/motivation/streaks/technique");

        streakRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot s) {

                int streak = s.child("streakCount").getValue(Integer.class) != null
                        ? s.child("streakCount").getValue(Integer.class) : 0;

                streak++;

                Map<String, Object> update = new HashMap<>();
                update.put("streakCount", streak);
                update.put("lastUpdated", System.currentTimeMillis());

                streakRef.updateChildren(update).addOnSuccessListener(a -> callback.done());
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void evaluatePerfectControllerWeek() {
        String childId = getChildId();
        if (childId == null) return;

        DatabaseReference thresholdRef = FirebaseDatabase.getInstance()
                .getReference("users/children/" + childId + "/medicine/motivation/thresholds/perfectWeekGoal");

        thresholdRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot tSnap) {

                int threshold = tSnap.getValue(Integer.class) != null
                        ? tSnap.getValue(Integer.class) : 7;

                DatabaseReference streakRef = FirebaseDatabase.getInstance()
                        .getReference("users/children/" + childId + "/medicine/motivation/streaks/controller/streakCount");

                streakRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot sSnap) {

                        int streak = sSnap.getValue(Integer.class) != null
                                ? sSnap.getValue(Integer.class) : 0;

                        if (streak >= threshold) {
                            FirebaseDatabase.getInstance()
                                    .getReference("users/children/" + childId + "/medicine/motivation/earned/perfectControllerWeek")
                                    .setValue(true);
                        }
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void evaluateTechniqueMaster() {

        String childId = getChildId();
        if (childId == null) return;

        DatabaseReference thresholdRef = FirebaseDatabase.getInstance()
                .getReference("users/children/" + childId + "/medicine/motivation/thresholds/techniqueGoal");

        thresholdRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot tSnap) {

                int threshold = tSnap.getValue(Integer.class) != null
                        ? tSnap.getValue(Integer.class) : 10;

                DatabaseReference streakRef = FirebaseDatabase.getInstance()
                        .getReference("users/children/" + childId + "/medicine/motivation/streaks/technique/streakCount");

                streakRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot sSnap) {

                        int streak = sSnap.getValue(Integer.class) != null
                                ? sSnap.getValue(Integer.class) : 0;

                        if (streak >= threshold) {
                            FirebaseDatabase.getInstance()
                                    .getReference("users/children/" + childId + "/medicine/motivation/earned/techniqueMaster")
                                    .setValue(true);
                        }
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // =========================================================================
    // LOW RESCUE MONTH BADGE
    // =========================================================================
    private void evaluateLowRescueMonth() {
        String childId = getChildId();
        if (childId == null) return;

        long cutoff = System.currentTimeMillis() - 30L * 24L * 60L * 60L * 1000;

        DatabaseReference rescueRef = FirebaseDatabase.getInstance()
                .getReference("users/children/" + childId + "/medicine/rescue");

        rescueRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot ds) {

                int rescueCount = 0;

                for (DataSnapshot log : ds.getChildren()) {
                    Long ts = log.child("timestamp").getValue(Long.class);
                    if (ts != null && ts >= cutoff) rescueCount++;
                }

                DatabaseReference thresholdRef = FirebaseDatabase.getInstance()
                        .getReference("users/children/" + childId + "/medicine/motivation/thresholds/lowRescueGoal");

                int finalRescueCount = rescueCount;
                thresholdRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot tSnap) {

                        int threshold = tSnap.getValue(Integer.class) != null
                                ? tSnap.getValue(Integer.class) : 4;

                        if (finalRescueCount <= threshold) {
                            FirebaseDatabase.getInstance()
                                    .getReference("users/children/" + childId + "/medicine/motivation/earned/lowRescueMonth")
                                    .setValue(true);
                        }
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

}
