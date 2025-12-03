package com.example.cscb07_final_project_smartair.Models;

import androidx.annotation.NonNull;

import com.example.cscb07_final_project_smartair.Presenters.ProviderHomePresenter;
import com.example.cscb07_final_project_smartair.Users.ChildPermissions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProviderHomeModel {
    private final ProviderHomePresenter presenter;

    public ProviderHomeModel(ProviderHomePresenter presenter) {
        this.presenter = presenter;
    }

    // loads provider info
    public void linkChildWithCode(String inviteCode) {
        FirebaseUser provider = FirebaseAuth.getInstance().getCurrentUser();
        if (provider == null) {
            presenter.onFailure("User not logged in.");
            return;
        }

        String providerId = provider.getUid();
        DatabaseReference providerNameRef = FirebaseDatabase.getInstance()
                .getReference("users/providers")
                .child(providerId)
                .child("name");

        providerNameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot nameSnap) {
                String providerName = nameSnap.getValue(String.class);
                if (providerName == null || providerName.trim().isEmpty()) {
                    providerName = "Provider";
                }

                proceedWithLink(inviteCode, providerId, providerName);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                presenter.onFailure("Failed to load provider name.");
            }
        });
    }

    // find child with matching invite code, adds child id to providers branch
    // and sets default permissions for provider in child branch
    private void proceedWithLink(String inviteCode, String providerId, String providerName) {
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

                        DatabaseReference providerChildRef = FirebaseDatabase.getInstance()
                                .getReference("users/providers")
                                .child(providerId)
                                .child("children")
                                .child(childId);

                        providerChildRef.setValue(true)
                                .addOnSuccessListener(a -> {
                                    DatabaseReference permRef = FirebaseDatabase.getInstance()
                                            .getReference("users/children")
                                            .child(childId)
                                            .child("sharingPerms")
                                            .child(providerId);

                                    ChildPermissions defaultPerms =
                                            ChildPermissions.getDefault(providerName, providerId);

                                    permRef.setValue(defaultPerms)
                                            .addOnSuccessListener(x -> presenter.onChildLinkedSuccess())
                                            .addOnFailureListener(e -> presenter.onFailure(e.getMessage()));
                                })
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
