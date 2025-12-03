package com.example.cscb07_final_project_smartair.Models;

import androidx.annotation.NonNull;

import com.example.cscb07_final_project_smartair.R;
import com.example.cscb07_final_project_smartair.DataObjects.Badge;
import com.example.cscb07_final_project_smartair.DataObjects.BadgeThresholds;
import com.example.cscb07_final_project_smartair.Presenters.BadgeSettingsPresenter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class BadgeSettingsModel {
    private final BadgeSettingsPresenter presenter;

    public BadgeSettingsModel(BadgeSettingsPresenter presenter) {
        this.presenter = presenter;
    }

    public void loadChildren() {
        //get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            presenter.onFailure("User not logged in.");
            return;
        }

        String parentID = user.getUid();
        //get children of current user
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child("parents")
                .child(parentID)
                .child("children");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> childIDs = new ArrayList<>();
                List<String> childNames = new ArrayList<>();

                //iterate through children, create list
                for (DataSnapshot ds : snapshot.getChildren()) {
                    childIDs.add(ds.getKey());
                    String name = ds.child("name").getValue(String.class);
                    if (name == null) name = "(Unnamed)";
                    childNames.add(name);
                }

                presenter.onChildrenLoaded(childIDs, childNames);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                presenter.onFailure(error.getMessage());
            }
        });
    }

    public void loadThresholds(String childID) {
        //get current thresholds
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child("children")
                .child(childID)
                .child("medicine")
                .child("motivation")
                .child("thresholds");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                BadgeThresholds t = snapshot.getValue(BadgeThresholds.class);

                if (t == null) {
                    t = BadgeThresholds.getDefault();
                }

                presenter.onThresholdsLoaded(t);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                presenter.onFailure(error.getMessage());
            }
        });
    }

    public void saveThresholds(String childID, BadgeThresholds thresholds) {
        //get current threshold
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child("children")
                .child(childID)
                .child("medicine")
                .child("motivation")
                .child("thresholds");

        //update thresholds
        ref.setValue(thresholds)
                .addOnSuccessListener(a -> presenter.onSaveSuccess())
                .addOnFailureListener(e -> presenter.onFailure(e.getMessage()));
    }

    public void loadBadges(String childID) {
        //get current badges earned
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child("children")
                .child(childID)
                .child("medicine")
                .child("motivation")
                .child("earned");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //create list of badges
                List<Badge> list = new ArrayList<>();

                Boolean perfect = snapshot.child("perfectControllerWeek").getValue(Boolean.class);
                Boolean technique = snapshot.child("techniqueMaster").getValue(Boolean.class);
                Boolean lowRescue = snapshot.child("lowRescueMonth").getValue(Boolean.class);

                if (perfect != null && perfect) {
                    //create perfect week badge
                    list.add(new Badge(
                            "Perfect Controller Week",
                            "Completed a full week of controller doses.",
                            R.drawable.ic_badge_perfect_week
                    ));
                }

                if (technique != null && technique) {
                    //create technique master badge
                    list.add(new Badge(
                            "Technique Master",
                            "Completed 10 high-quality technique sessions.",
                            R.drawable.ic_badge_technique
                    ));
                }

                if (lowRescue != null && lowRescue) {
                    //create low rescue badge
                    list.add(new Badge(
                            "Low Rescue Month",
                            "Stayed below rescue usage threshold for 30 days.",
                            R.drawable.ic_badge_low_rescue
                    ));
                }

                presenter.onBadgesLoaded(list);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                presenter.onFailure(error.getMessage());
            }
        });
    }
}
