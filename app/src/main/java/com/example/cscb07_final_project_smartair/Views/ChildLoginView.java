package com.example.cscb07_final_project_smartair.Views;

public interface ChildLoginView {

    String getEmail();
    String getPassword();

    void showLoginSuccess(String message);
    void showLoginFailure(String message);
    void showValidationError(String message);

    void navigateToMainScreen();
    void navigateToSignUpScreen();

    void showPasswordResetSuccess(String message);
    void showPasswordResetFailure(String message);
}
