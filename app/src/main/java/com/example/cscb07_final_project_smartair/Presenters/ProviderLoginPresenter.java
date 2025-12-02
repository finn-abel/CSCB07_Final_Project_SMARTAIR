package com.example.cscb07_final_project_smartair.Presenters;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.cscb07_final_project_smartair.Models.LoginModel;
import com.example.cscb07_final_project_smartair.Models.ProviderLoginModel;
import com.example.cscb07_final_project_smartair.Views.LoginView;
import com.example.cscb07_final_project_smartair.Views.ProviderLoginView;
import com.google.firebase.auth.FirebaseAuth;

public class ProviderLoginPresenter implements ProviderLoginModel.OnLoginFinishedListener, ProviderLoginModel.OnResetPasswordFinishedListener {

    private ProviderLoginView view;
    private ProviderLoginModel model;
    private FirebaseAuth mAuth;

    public ProviderLoginPresenter(ProviderLoginView view) {
        this.view = view;
        this.model = new ProviderLoginModel();
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


           /* SharedPreferences prefs = view.getContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            editor.putString("USER_ROLE", "PROVIDER");
            editor.apply();
           */

            view.showLoginSuccess("Sign in successful!");
            view.navigateToProviderHomeScreen();
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
