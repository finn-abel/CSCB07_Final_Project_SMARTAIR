package com.example.cscb07_final_project_smartair.Presenters;

import com.example.cscb07_final_project_smartair.Models.LauncherModel;
import com.example.cscb07_final_project_smartair.Views.LauncherView;

/**
 * The Presenter for the Launcher screen. It determines which
 * screen should be displayed based on whether or not the user is logged in.
 */
public class LauncherPresenter {

    private final LauncherView view;
    private final LauncherModel model;

    public LauncherPresenter(LauncherView view) {
        this.view = view;
        this.model = new LauncherModel();
    }

    public void decideNextActivity() {
        if (!model.isUserLoggedIn()) {
            view.navigateToRoleLauncherScreen();
        }
        else {
            view.navigateToMainScreen();
        }
    }
}