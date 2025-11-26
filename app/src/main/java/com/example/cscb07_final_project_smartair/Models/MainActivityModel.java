package com.example.cscb07_final_project_smartair.Models;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivityModel {

    private FirebaseAuth mAuth;

    public MainActivityModel() {
        mAuth = FirebaseAuth.getInstance();
    }

    public void signOut() {
        mAuth.signOut();
    }

    //redirect to onboarding
    //redirect to child/parent home
}
