package com.example.cscb07_final_project_smartair.Models;

import androidx.annotation.NonNull;

import com.example.cscb07_final_project_smartair.DataObjects.Badge;
import com.example.cscb07_final_project_smartair.DataObjects.BadgeThresholds;
import com.example.cscb07_final_project_smartair.Presenters.BadgeSettingsPresenter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class BadgeSettingsModel {
    private final BadgeSettingsPresenter presenter;

    public BadgeSettingsModel(BadgeSettingsPresenter presenter) {
        this.presenter = presenter;
    }

    public void loadChildren() {
        String parentID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("childrenOfParent")
                .child(parentID);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> childIDs = new ArrayList<>();
                List<String> childNames = new ArrayList<>();

                for (DataSnapshot ds : snapshot.getChildren())
                {
                    childIDs.add(ds.getKey());
                    childNames.add(ds.child("name").getValue(String.class));
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
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("badgeThresholds")
                .child(childID);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                BadgeThresholds t = snapshot.getValue(BadgeThresholds.class);

                if (t == null)
                {
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
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("badgeThresholds")
                .child(childID);

        ref.setValue(thresholds)
                .addOnSuccessListener(a -> presenter.onSaveSuccess())
                .addOnFailureListener(e -> presenter.onFailure(e.getMessage()));
    }

    public void loadBadges(String childID) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("earnedBadges")
                .child(childID);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Badge> list = new ArrayList<>();

                for (DataSnapshot ds : snapshot.getChildren())
                {
                    Badge b = ds.getValue(Badge.class);
                    if (b != null)
                    {
                        list.add(b);
                    }
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
