package com.example.cscb07_final_project_smartair.Models;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * The Model for the app launcher. Launcher simply checks if a user is logged in.
 */
public class LauncherModel {

    private final FirebaseAuth mAuth;

    public LauncherModel() {
        this.mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Checks if a user is currently signed in.
     * @return true if a user is signed in, false otherwise.
     */
    public boolean isUserLoggedIn() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        return currentUser != null;
    }

}

