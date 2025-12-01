package com.example.cscb07_final_project_smartair.Views;

import android.content.Context;

// The View interface defines all the actions the Presenter can ask the View to perform.
public interface LoginView {
    String getEmail();
    String getPassword();

    void showLoginSuccess(String message);
    void showLoginFailure(String message);
    void showValidationError(String message);

    void navigateToMainScreen();
    void navigateToSignUpScreen();

    void showPasswordResetSuccess(String message);
    void showPasswordResetFailure(String message);

    Context getContext();
}

