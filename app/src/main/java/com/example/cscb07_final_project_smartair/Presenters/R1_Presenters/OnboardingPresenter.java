package com.example.cscb07_final_project_smartair.Presenters.R1_Presenters;

import com.example.cscb07_final_project_smartair.Views.R1_Views.OnboardingView;

public class OnboardingPresenter {
    private OnboardingView view;

    public OnboardingPresenter(OnboardingView view) {
        this.view = view;
    }

    public void onContinueClicked() {
        if (view != null) view.navigateToMainScreen();
    }

    public void onDestroy() {
        view = null;
    }
}

