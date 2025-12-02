package com.example.cscb07_final_project_smartair.Presenters;

import com.example.cscb07_final_project_smartair.Models.ProviderHomeModel;
import com.example.cscb07_final_project_smartair.Views.ProviderHomeView;

public class ProviderHomePresenter {
    private final ProviderHomeView view;
    private final ProviderHomeModel model;

    public ProviderHomePresenter(ProviderHomeView view){
        this.view = view;
        this.model = new ProviderHomeModel(this);
    }

    public void onLogoutButtonClicked() {
        view.navigateToRoleSelectionScreen();
    }

    public void onProviderReportButtonClicked() {
        // TODO: navigate to provider report selection screen
    }

    public void onLinkChildClicked() {
        String code = view.getInviteCodeInput();

        if (code.isEmpty()) {
            view.showError("Please enter an invite code.");
            return;
        }

        model.linkChildWithCode(code);
    }

    public void onChildLinkedSuccess() {
        view.clearInviteCodeInput();
        view.showSuccess("Child linked successfully.");
    }

    public void onFailure(String msg) {
        view.showError(msg);
    }
}
