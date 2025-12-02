package com.example.cscb07_final_project_smartair.Models;

import androidx.annotation.NonNull;

import com.example.cscb07_final_project_smartair.DataObjects.Invite;
import com.example.cscb07_final_project_smartair.Presenters.InvitesPresenter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class InvitesModel {
    private final InvitesPresenter presenter;
    private final Random rand = new Random();

    public InvitesModel(InvitesPresenter presenter) {
        this.presenter = presenter;
    }

    public void loadChildren() {
        FirebaseUser parent = FirebaseAuth.getInstance().getCurrentUser();

        if (parent == null) {
            presenter.onFailure("User not logged in.");
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users/parents/" + parent.getUid() + "/children");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snap) {

                List<String> ids = new ArrayList<>();
                List<String> names = new ArrayList<>();

                for (DataSnapshot child : snap.getChildren()) {
                    ids.add(child.getKey());
                    names.add(child.child("name").getValue(String.class));
                }

                presenter.onChildrenLoaded(ids, names);
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {
                presenter.onFailure(error.getMessage());
            }
        });
    }

    public void loadInvitesForChild(String childId) {

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users/children/" + childId + "/invites");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snap) {

                List<Invite> invites = new ArrayList<>();

                for (DataSnapshot ds : snap.getChildren()) {
                    Invite inv = new Invite();
                    inv.inviteCode = ds.getKey();
                    inv.childId = childId;
                    inv.expiresAt = ds.getValue(Long.class);
                    invites.add(inv);
                }

                presenter.onInvitesLoaded(invites);
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {
                presenter.onFailure(error.getMessage());
            }
        });
    }

    public void generateInvite(String childId) {
        // Generate 10-char random code
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            int t = rand.nextInt(3);
            if (t == 0) sb.append((char)(65 + rand.nextInt(26)));
            else if (t == 1) sb.append((char)(97 + rand.nextInt(26)));
            else sb.append(rand.nextInt(10));
        }

        String code = sb.toString();
        long exp = System.currentTimeMillis() + 604800000L;

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users/children/" + childId + "/invites")
                .child(code);

        ref.setValue(exp)
                .addOnSuccessListener(a ->
                        presenter.onInviteGenerated(new Invite(code, childId, exp)))
                .addOnFailureListener(e ->
                        presenter.onFailure(e.getMessage()));
    }

    public void revokeInvite(String childId, String code) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users/children/" + childId + "/invites")
                .child(code);

        ref.removeValue()
                .addOnSuccessListener(a ->
                        presenter.onInviteRevoked(code))
                .addOnFailureListener(e ->
                        presenter.onFailure(e.getMessage()));
    }
}
