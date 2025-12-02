package com.example.cscb07_final_project_smartair.Views;

public interface ProviderHomeView {
    void navigateToRoleSelectionScreen();

    String getInviteCodeInput();
    void clearInviteCodeInput();

    void showSuccess(String msg);
    void showError(String msg);
}
