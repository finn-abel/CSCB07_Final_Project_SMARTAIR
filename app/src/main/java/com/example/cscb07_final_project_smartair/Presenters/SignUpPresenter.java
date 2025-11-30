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

    public void onPasswordEdit(String password) {
        int strength = model.passwordcheck(password);

        if(strength == 1){
            view.clearPasswordError();
            view.enableSignUp(true);
        } else if (strength == 0){
            view.showPasswordError("Password is too short");
            view.enableSignUp(false);
        } else {
            view.showPasswordError(
                    "Password invalid. Passwords must have at least 8 characters," +
                            "at least one upper and lower case character, " +
                            "one number, and at least one symbol of !@#$%&"
            );
            view.enableSignUp(false);
        }

    }
    public void onSignUpButtonClicked() {
        String email = view.getEmail();
        String password = view.getPassword();
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

