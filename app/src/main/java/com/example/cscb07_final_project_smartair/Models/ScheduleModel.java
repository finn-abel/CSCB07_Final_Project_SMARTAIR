package com.example.cscb07_final_project_smartair.Models;

import androidx.annotation.NonNull;

import com.example.cscb07_final_project_smartair.DataObjects.ScheduleEntry;
import com.example.cscb07_final_project_smartair.Presenters.SchedulePresenter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.*;

public class ScheduleModel {
    private SchedulePresenter presenter;

    public ScheduleModel(SchedulePresenter presenter) {
        this.presenter = presenter;
    }

    public void fetchChildren() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            presenter.onFailure("User not logged in.");
            return;
        }

        String parentId = user.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child("parents")
                .child(parentId)
                .child("children");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> names = new ArrayList<>();
                List<String> ids = new ArrayList<>();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    ids.add(ds.getKey());
                    String name = ds.child("name").getValue(String.class);
                    if (name == null) name = "(Unnamed)";
                    names.add(name);
                }

                presenter.onChildrenLoaded(names, ids);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                presenter.onFailure(error.getMessage());
            }
        });
    }


    public void fetchScheduleDay(String childId, String day) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("medicine")
                .child("schedule")
                .child(childId)
                .child(day);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snap) {
                List<ScheduleEntry> entries = new ArrayList<>();

                for (DataSnapshot ds : snap.getChildren())
                {
                    ScheduleEntry entry = ds.getValue(ScheduleEntry.class);
                    if (entry != null) entries.add(entry);
                }

                presenter.onDayScheduleLoaded(entries);
            }
            @Override public void onCancelled(@NonNull DatabaseError err) {
                presenter.onFailure(err.getMessage());
            }
        });
    }

    public void saveScheduleEntry(String childId, String day, ScheduleEntry newEntry,
                                  ScheduleEntry oldEntry) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("medicine")
                .child("schedule")
                .child(childId)
                .child(day);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snap) {
                List<ScheduleEntry> entries = new ArrayList<>();

                for (DataSnapshot ds : snap.getChildren()) {
                    ScheduleEntry entry = ds.getValue(ScheduleEntry.class);

                    if (entry == null) continue;
                    // remove existing
                    if (oldEntry != null && entry.time.equals(oldEntry.time) &&
                            entry.doseAmount == oldEntry.doseAmount &&
                            Objects.equals(entry.note, oldEntry.note))
                    {
                        continue;
                    }
                    entries.add(entry);
                }
                entries.add(newEntry);

                ref.setValue(entries)
                        .addOnSuccessListener(v -> presenter.onEntrySaved())
                        .addOnFailureListener(e -> presenter.onFailure(e.getMessage()));
            }
            @Override public void onCancelled(@NonNull DatabaseError err) {
                presenter.onFailure(err.getMessage());
            }
        });
    }

    public void deleteScheduleEntry(String childId, String day, ScheduleEntry target) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("medicine")
                .child("schedule")
                .child(childId)
                .child(day);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snap) {
                List<ScheduleEntry> updated = new ArrayList<>();

                for (DataSnapshot ds : snap.getChildren()) {
                    ScheduleEntry entry = ds.getValue(ScheduleEntry.class);

                    if (entry == null) continue;

                    if (entry.time.equals(target.time) && entry.doseAmount == target.doseAmount
                            && Objects.equals(entry.note, target.note))
                    {
                        continue;
                    }

                    updated.add(entry);
                }

                ref.setValue(updated)
                        .addOnSuccessListener(v -> presenter.onEntryDeleted())
                        .addOnFailureListener(e -> presenter.onFailure(e.getMessage()));
            }
            @Override public void onCancelled(@NonNull DatabaseError err) {
                presenter.onFailure(err.getMessage());
            }
        });
    }
}
