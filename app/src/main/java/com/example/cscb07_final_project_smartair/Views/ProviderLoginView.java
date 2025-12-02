package com.example.cscb07_final_project_smartair.Views;

import android.content.Context;

public interface ProviderLoginView {

    String getEmail();
    String getPassword();

    void showLoginSuccess(String message);
    void showLoginFailure(String message);
    void showValidationError(String message);

    void navigateToProviderHomeScreen();
    void navigateToSignUpScreen();

    void showPasswordResetSuccess(String message);
    void showPasswordResetFailure(String message);

    Context getContext();
}
