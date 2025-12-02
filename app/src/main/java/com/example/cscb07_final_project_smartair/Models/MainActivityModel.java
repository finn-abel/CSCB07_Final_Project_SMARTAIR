package com.example.cscb07_final_project_smartair.Models;

import androidx.annotation.NonNull;

import com.example.cscb07_final_project_smartair.DataObjects.Badge;
import com.example.cscb07_final_project_smartair.DataObjects.ScheduleEntry;
import com.example.cscb07_final_project_smartair.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivityModel {

    private FirebaseAuth mAuth;

    public MainActivityModel() {
        mAuth = FirebaseAuth.getInstance();
    }

    public void signOut() {
        mAuth.signOut();
    }

    public void loadBadgesAndStreaks(MainDataCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }

        String childId = user.getUid();
        if (childId == null) {
            callback.onFailure("Child ID is null");
            return;
        }

        loadBadges(childId, callback);
        loadStreaks(childId, callback);
        loadNextDose(childId, callback);
    }

    private void loadBadges(String childId, MainDataCallback callback) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child("children")
                .child(childId)
                .child("medicine")
                .child("motivation")
                .child("earned");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<Badge> list = new ArrayList<>();

                Boolean perfect = snapshot.child("perfectControllerWeek").getValue(Boolean.class);
                Boolean technique = snapshot.child("techniqueMaster").getValue(Boolean.class);
                Boolean lowRescue = snapshot.child("lowRescueMonth").getValue(Boolean.class);

                if (perfect != null && perfect) {
                    list.add(new Badge(
                            "Perfect Controller Week",
                            "Completed a full week of controller doses.",
                            R.drawable.ic_badge_perfect_week
                    ));
                }

                if (technique != null && technique) {
                    list.add(new Badge(
                            "Technique Master",
                            "Completed 10 high-quality technique sessions.",
                            R.drawable.ic_badge_technique
                    ));
                }

                if (lowRescue != null && lowRescue) {
                    list.add(new Badge(
                            "Low Rescue Month",
                            "Stayed below rescue usage threshold for 30 days.",
                            R.drawable.ic_badge_low_rescue
                    ));
                }

                callback.onBadgesLoaded(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    private void loadStreaks(String childId, MainDataCallback callback) {
        DatabaseReference streakRef = FirebaseDatabase.getInstance()
                .getReference("users/children/" + childId + "/medicine/motivation/streaks");

        streakRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot s) {
                int controller = 0;
                int technique = 0;

                DataSnapshot controllerNode = s.child("controller");
                DataSnapshot techniqueNode = s.child("technique");

                if (controllerNode.exists()) {
                    Integer c = controllerNode.child("streakCount").getValue(Integer.class);
                    controller = (c != null) ? c : 0;
                }

                if (techniqueNode.exists()) {
                    Integer t = techniqueNode.child("streakCount").getValue(Integer.class);
                    technique = (t != null) ? t : 0;
                }

                callback.onStreaksLoaded(controller, technique);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    private void loadNextDose(String childId, MainDataCallback callback) {
        String today = new SimpleDateFormat("EEEE", Locale.getDefault()).format(new Date());

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child("children")
                .child(childId)
                .child("medicine")
                .child("schedule")
                .child(today);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.exists()) {
                    callback.onNextDoseLoaded("No doses scheduled today.");
                    return;
                }

                List<ScheduleEntry> entries = new ArrayList<>();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    ScheduleEntry e = snap.getValue(ScheduleEntry.class);
                    if (e != null) entries.add(e);
                }

                if (entries.isEmpty()) {
                    callback.onNextDoseLoaded("No doses scheduled for today.");
                    return;
                }

                Calendar now = Calendar.getInstance();
                int currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE);

                ScheduleEntry next = null;
                int min = Integer.MAX_VALUE;

                for (ScheduleEntry e : entries) {
                    try {
                        String[] parts = e.time.split(":");
                        int h = Integer.parseInt(parts[0]);
                        int m = Integer.parseInt(parts[1]);
                        int total = h * 60 + m;

                        if (total >= currentMinutes && total < min) {
                            min = total;
                            next = e;
                        }
                    } catch (Exception ignored) {}
                }

                if (next == null) {
                    callback.onNextDoseLoaded("No more doses today.");
                } else {
                    callback.onNextDoseLoaded("Next Dose: " + next.time + " (" + next.doseAmount + " puffs)");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onNextDoseLoaded("Unable to load next dose.");
            }
        });
    }
    public interface MainDataCallback {
        void onBadgesLoaded(List<Badge> badges);
        void onStreaksLoaded(int controllerStreak, int techniqueStreak);
        void onNextDoseLoaded(String nextDoseText);
        void onFailure(String error);
    }

}
