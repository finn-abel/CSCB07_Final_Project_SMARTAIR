package com.example.cscb07_final_project_smartair.Models;

import androidx.annotation.NonNull;

import com.example.cscb07_final_project_smartair.DataObjects.InventoryItem;
import com.example.cscb07_final_project_smartair.Presenters.InventoryPresenter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class InventoryModel {
    private final InventoryPresenter presenter;

    public InventoryModel(InventoryPresenter presenter) {
        this.presenter = presenter;
    }

    public void getChildren() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            presenter.onFailure("User not logged in.");
            return;
        }

        String parentID = user.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("parents")
                .child(parentID)
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
                //TESTING
                names.add("Test Child");
                ids.add("l1Z0u0INnMZxsjae4MdRCOj8oqJ3");

                presenter.onChildrenLoaded(names, ids);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                presenter.onFailure(error.getMessage());
            }
        });
    }

    public void getInventory(String childId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            presenter.onFailure("User not logged in.");
            return;
        }

        String parentID = user.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("parents")
                .child(parentID)
                .child("children")
                .child(childId)
                .child("inventory");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<InventoryItem> list = new ArrayList<>();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    InventoryItem item = ds.getValue(InventoryItem.class);
                    if (item != null) list.add(item);
                }

                presenter.onInventoryLoaded(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                presenter.onFailure(error.getMessage());
            }
        });
    }

    public void saveItem(String childId, InventoryItem item) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            presenter.onFailure("User not logged in.");
            return;
        }
        String parentID = user.getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("parents")
                .child(parentID)
                .child("children")
                .child(childId)
                .child("inventory")
                .child(item.medicationName);

        ref.setValue(item)
                .addOnSuccessListener(aVoid -> presenter.onSaveSuccess())
                .addOnFailureListener(e -> presenter.onFailure(e.getMessage()));
    }

    public void deleteItem(String childId, String medicationName) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            presenter.onFailure("User not logged in.");
            return;
        }
        String parentID = user.getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("parents")
                .child(parentID)
                .child("children")
                .child(childId)
                .child("inventory")
                .child(medicationName);

        ref.removeValue()
                .addOnSuccessListener(aVoid -> presenter.onDeleteSuccess())
                .addOnFailureListener(e -> presenter.onFailure(e.getMessage()));
    }
}
