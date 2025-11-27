package com.example.cscb07_final_project_smartair.Presenters;

import com.example.cscb07_final_project_smartair.Views.OnboardingActivity;

public class OnboardingPresenter {
    private OnboardingActivity view;

    public OnboardingPresenter(OnboardingActivity view) {
        this.view = view;
    }

    public void onContinueClicked() {
        if (view != null) view.navigateToMainScreen();
    }

    public void onDestroy() {
        view = null;
    }
}

