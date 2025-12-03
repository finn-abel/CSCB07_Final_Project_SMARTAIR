package com.example.cscb07_final_project_smartair.Models;

import com.google.firebase.auth.FirebaseAuth;

public class ProviderReportModel extends BaseModel {
    private FirebaseAuth mAuth;

    public ProviderReportModel() {
        mAuth = FirebaseAuth.getInstance();
    }

    public String getCurrentUserId() {
        if (mAuth.getCurrentUser() != null) {
            return mAuth.getCurrentUser().getUid();
        }

        else {
            return null;
        }
    }
}
