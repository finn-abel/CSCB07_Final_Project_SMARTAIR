package com.example.cscb07_final_project_smartair.Models;

import com.google.firebase.auth.FirebaseAuth;

public class ParentHomeModel extends BaseModel {
    private FirebaseAuth mAuth;

    public ParentHomeModel() {
        mAuth = FirebaseAuth.getInstance();
    }

    public void signOut() {
        mAuth.signOut();
    }

    public String getCurrentUserId() {
        if (mAuth.getCurrentUser() != null) {
            return mAuth.getCurrentUser().getUid();
        }

        else {
            return null;
        }
    }

    //redirect to onboarding
    //redirect to child/parent home
}
