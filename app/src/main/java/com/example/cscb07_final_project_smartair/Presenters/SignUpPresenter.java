package com.example.cscb07_final_project_smartair.Presenters;

import com.example.cscb07_final_project_smartair.Models.SignUpModel;
import com.example.cscb07_final_project_smartair.Views.SignUpView;

public class SignUpPresenter implements SignUpModel.OnSignUpFinishedListener {

    private final SignUpView view;
    private final SignUpModel model;

    public SignUpPresenter(SignUpView view) {
        this.view = view;
        this.model = new SignUpModel();
    }

    public void onSignUpButtonClicked() {
        String email = view.getEmail();
        String password = view.getPassword();

        // Input Validation by Liam to go here

        model.createUser(email, password, this);
    }

    public void onSignInButtonClicked() {
        view.navigateToLoginScreen();

    }


    @Override
    public void onSignUpSuccess() {
        if (view != null) {
            view.showSignUpSuccess("Account created successfully! Please log in.");
            view.navigateToLoginScreen();
        }
    }

    @Override
    public void onSignUpFailure(String errorMessage) {
        if (view != null) {
            view.showSignUpFailure(errorMessage);
        }
    }
}

