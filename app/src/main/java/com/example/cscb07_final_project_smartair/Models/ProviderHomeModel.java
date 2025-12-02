package com.example.cscb07_final_project_smartair.Models;

import androidx.annotation.NonNull;

import com.example.cscb07_final_project_smartair.Presenters.ProviderHomePresenter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class ProviderHomeModel {
    private final ProviderHomePresenter presenter;

    public ProviderHomeModel(ProviderHomePresenter presenter) {
        this.presenter = presenter;
    }

    public void linkChildWithCode(String inviteCode) {
        FirebaseUser provider = FirebaseAuth.getInstance().getCurrentUser();
        if (provider == null) {
            presenter.onFailure("User not logged in.");
            return;
        }

        String providerId = provider.getUid();
        DatabaseReference childrenRef = FirebaseDatabase.getInstance()
                .getReference("users/children");

        childrenRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean found = false;

                for (DataSnapshot childSnap : snapshot.getChildren()) {
                    String childId = childSnap.getKey();

                    if (childSnap.child("invites").hasChild(inviteCode)) {
                        Long expiresAt = childSnap.child("invites")
                                .child(inviteCode)
                                .getValue(Long.class);

                        long now = System.currentTimeMillis();
                        if (expiresAt != null && expiresAt < now) {
                            presenter.onFailure("Invite code has expired.");
                            return;
                        }

                        // Link child under provider
                        DatabaseReference providerChildRef = FirebaseDatabase.getInstance()
                                .getReference("users/providers")
                                .child(providerId)
                                .child("children")
                                .child(childId);

                        providerChildRef.setValue(true)
                                .addOnSuccessListener(a -> presenter.onChildLinkedSuccess())
                                .addOnFailureListener(e -> presenter.onFailure(e.getMessage()));
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    presenter.onFailure("No child found for that invite code.");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                presenter.onFailure(error.getMessage());
            }
        });
    }
}
