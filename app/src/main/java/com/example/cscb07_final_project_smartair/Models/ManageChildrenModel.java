package com.example.cscb07_final_project_smartair.Models;

import androidx.annotation.NonNull;

import com.example.cscb07_final_project_smartair.Presenters.ManageChildrenPresenter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.cscb07_final_project_smartair.Users.Child;
import com.example.cscb07_final_project_smartair.Users.ChildPermissions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ManageChildrenModel {
    private final ManageChildrenPresenter presenter;

    public ManageChildrenModel(ManageChildrenPresenter presenter) {
        this.presenter = presenter;
    }

    public void getChildren() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            presenter.onFailure("User not logged in.");
            return;
        }

        String parentID = user.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users")
                .child("parents")
                .child(user.getUid())
                .child("children");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> names = new ArrayList<>();
                List<String> ids = new ArrayList<>();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    String name = ds.child("name").getValue(String.class);
                    names.add(name);
                    ids.add(ds.getKey());
                }

                presenter.onChildrenLoaded(names, ids);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                presenter.onFailure(error.getMessage());
            }
        });
    }

    public void getProviders(String childId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            presenter.onFailure("User not logged in.");
            return;
        }

        String parentID = user.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child("children")
                .child(childId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Child child = snapshot.getValue(Child.class);
                presenter.onProvidersLoaded(child);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                presenter.onFailure(error.getMessage());
            }
        });
    }

    public void setPerms(String childId, Map<String, ChildPermissions> permMap) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            presenter.onFailure("User not logged in.");
            return;
        }

        String parentID = user.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child("children")
                .child(childId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Child child = snapshot.getValue(Child.class);
                child.sharingPerms = permMap;
                ref.setValue(child)
                        .addOnFailureListener(e -> presenter.onFailure(e.getMessage()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                presenter.onFailure(error.getMessage());
            }
        });
    }
}
