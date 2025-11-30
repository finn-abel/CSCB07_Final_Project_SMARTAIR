package com.example.cscb07_final_project_smartair.Presenters;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.cscb07_final_project_smartair.Models.LoginModel;
import com.example.cscb07_final_project_smartair.Views.LoginView;

public class LoginPresenter implements LoginModel.OnLoginFinishedListener, LoginModel.OnResetPasswordFinishedListener {

    private LoginView view;
    private LoginModel model;

    public LoginPresenter(LoginView view) {
        this.view = view;
        this.model = new LoginModel();
    }

    public void onLoginButtonClicked() {
        String email = view.getEmail();
        String password = view.getPassword();

        model.signInUser(email, password, this);
    }

    public void onForgotPasswordButtonClicked() {
        String email = view.getEmail();
        model.sendPasswordResetEmail(email,this);
    }

    public void onSignUpButtonClicked() {

        view.navigateToSignUpScreen();
    }


    @Override
    public void onLoginSuccess() {
        if (view != null) {

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


