package com.example.cscb07_final_project_smartair.Views;

// This interface defines the contract for the Sign-Up screen.
public interface SignUpView {

    String getEmail();
    String getPassword();


    void showSignUpSuccess(String message);
    void showSignUpFailure(String message);


    void navigateToLoginScreen();
}

