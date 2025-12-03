package com.example.cscb07_final_project_smartair.Presenters;

import com.example.cscb07_final_project_smartair.Models.ChildLoginModel;
import com.example.cscb07_final_project_smartair.Models.LoginModel;
import com.example.cscb07_final_project_smartair.Views.ChildLoginView;
import com.example.cscb07_final_project_smartair.Views.LoginView;

public class ChildLoginPresenter implements LoginModel.OnLoginFinishedListener {

    private ChildLoginView view;
    private ChildLoginModel model;

    public ChildLoginPresenter(ChildLoginView view, ChildLoginModel model) {
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




    @Override
    public void onLoginSuccess() {
        if (view != null) {
            view.showLoginSuccess("Sign in successful!");
            view.navigateToMainScreen();
        }
    }


    @Override
    public void onLoginFailure(String errorMessage) {
        if (view != null) {
            view.showLoginFailure(errorMessage);
        }
    }
}


