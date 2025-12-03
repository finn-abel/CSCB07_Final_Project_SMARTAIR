package com.example.cscb07_final_project_smartair.DataObjects;

import com.google.firebase.auth.FirebaseAuth;

public class Invite extends Data {
    public String inviteCode;
    public String childId;
    public long expiresAt;

    public Invite() {
        //empty for FB
    }

    public Invite(String inviteCode, String childId, long expiresAt) {
        super(System.currentTimeMillis(), FirebaseAuth.getInstance().getCurrentUser().getUid(), "invite");
        this.inviteCode = inviteCode;
        this.childId = childId;
        this.expiresAt = expiresAt;
    }
}
