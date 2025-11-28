package com.example.cscb07_final_project_smartair.Presenters;

import com.example.cscb07_final_project_smartair.Models.LauncherModel;
import com.example.cscb07_final_project_smartair.Views.LauncherView;

/**
 * The Presenter for the Launcher screen. It decides which
 * screen to show based on the user's authentication state.
 */
public class LauncherPresenter {

    private final LauncherView view;
    private final LauncherModel model;

    public LauncherPresenter(LauncherView view) {
        this.view = view;
        this.model = new LauncherModel();
    }

    /**
     * Checks the login status with the model and tells the view where to navigate.
     * This is called as soon as the LauncherActivity starts.
     */
    public void decideNextActivity() {
        if (!model.isUserLoggedIn()) {
            view.navigateToLoginScreen();
        }
        else {
            view.navigateToMainScreen();
        }
    }
}