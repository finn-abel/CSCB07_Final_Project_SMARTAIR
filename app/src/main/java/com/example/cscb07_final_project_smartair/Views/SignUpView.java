package com.example.cscb07_final_project_smartair.Views;

// This interface defines the contract for the Sign-Up screen.
public interface SignUpView {

    String getEmail();
    String getPassword();

    void showPasswordError(String message); //shows a message when password is invalid or weak
    void clearPasswordError(); //clears password error message when valid/strong
    void enableSignUp(boolean status); //enables sign up button when password is valid

    void showSignUpSuccess(String message);
    void showSignUpFailure(String message);
    String getName();


    void navigateToLoginScreen();


}

