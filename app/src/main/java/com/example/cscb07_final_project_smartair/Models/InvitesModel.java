package com.example.cscb07_final_project_smartair.Models;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.cscb07_final_project_smartair.Presenters.InvitesPresenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class InvitesModel {
    private final InvitesPresenter presenter;
    private final FirebaseAuth mAuth;
    private final Random rand;

    public InvitesModel(InvitesPresenter presenter) {
        this.presenter = presenter;
        mAuth = FirebaseAuth.getInstance();
        rand = new Random();
    }

    public void getInvites() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            presenter.onFailure("User not logged in.");
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users")
                .child("parents")
                .child(user.getUid())
                .child("children");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> names = new ArrayList<>();
                List<String> ids = new ArrayList<>();
                List<Map<String, Long>> invites = new ArrayList<>();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    String name = ds.child("name").getValue(String.class);
                    names.add(name);
                    ids.add(ds.getKey());
                    Map<String, Long> invs = new HashMap<>();
                    for (DataSnapshot ds2 : ds.child("invites").getChildren())
                        invs.put(ds2.getKey(), ds2.getValue(Long.class));
                    invites.add(invs);
                }

                presenter.onInvitesLoaded(names, ids, invites);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                presenter.onFailure(error.getMessage());
            }
        });
    }

    public void generateInvite(String childId) {
        StringBuilder inv = new StringBuilder(10);
        int n;
        for (int i = 0; i < 10; i++) {
            n = rand.nextInt(3);
            if (n == 0) inv.append((char) (65 + rand.nextInt(26)));
            else if (n == 1) inv.append((char) (97 + rand.nextInt(26)));
            else inv.append(rand.nextInt(10));
        }
        String invite = inv.toString();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users")
                .child("parents")
                .child(user.getUid())
                .child("children")
                .child(childId)
                .child("invites")
                .child(invite);
        long time = System.currentTimeMillis() + 604800000L;
        ref.setValue(time)
                .addOnSuccessListener(aVoid -> presenter.onInviteGenerated(invite, time))
                .addOnFailureListener(e -> presenter.onFailure(e.getMessage()));
    }

    public void revokeInvite(String childId, String invite) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users")
                .child("parents")
                .child(user.getUid())
                .child("children")
                .child(childId)
                .child("invites")
                .child(invite);
        ref.removeValue()
                .addOnSuccessListener(aVoid -> presenter.onInviteRevoked(invite))
                .addOnFailureListener(e -> presenter.onFailure(e.getMessage()));
    }
}
