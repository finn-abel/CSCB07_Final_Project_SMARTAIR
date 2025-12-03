package com.example.cscb07_final_project_smartair.Presenters;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.cscb07_final_project_smartair.Models.LoginModel;
import com.example.cscb07_final_project_smartair.Views.LoginView;
import com.google.firebase.auth.FirebaseAuth;

public class LoginPresenter implements LoginModel.OnLoginFinishedListener, LoginModel.OnResetPasswordFinishedListener {

    private LoginView view;
    private LoginModel model;

    public LoginPresenter(LoginView view, LoginModel model) {
        this.view = view;
        this.model = model;
    }

    public void onLoginButtonClicked() {
        String email = view.getEmail();
        String password = view.getPassword();

        if (email.isEmpty() && password.isEmpty()) {
            view.showValidationError("Please enter login credentials.");
            return;
        }
        else if (email.isEmpty()) {
            view.showValidationError("Email cannot be empty.");
            return;
        }
        else if (password.isEmpty()) {
            view.showValidationError("Password cannot be empty.");
            return;
        }
        model.signInUser(email, password, this);
    }

    public void onForgotPasswordButtonClicked() {
        String email = view.getEmail();
        if (email.isEmpty()) {
            view.showPasswordResetFailure("Email cannot be empty.");
            return;
        }
        model.sendPasswordResetEmail(email,this);
    }

    public void onSignUpButtonClicked() {

        view.navigateToSignUpScreen();
    }


    @Override
    public void onLoginSuccess() {
        if (view != null) {


           /* SharedPreferences prefs = view.getContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            editor.putString("USER_ROLE", "PARENT");
            editor.apply();*/

            view.showLoginSuccess("Sign in successful!");
            view.navigateToMainScreen();
        }
    }

    @Override
    public void onResetPasswordSuccess(String message){
        view.showPasswordResetSuccess(message);
    }

    @Override
    public void onResetPasswordFailure(String errorMessage){
        view.showPasswordResetFailure(errorMessage);
    }

    @Override
    public void onLoginFailure(String errorMessage) {
        if (view != null) {
            view.showLoginFailure(errorMessage);
        }
    }


}


