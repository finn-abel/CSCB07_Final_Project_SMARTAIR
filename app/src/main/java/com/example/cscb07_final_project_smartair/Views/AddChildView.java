package com.example.cscb07_final_project_smartair.Views;

public interface AddChildView {
    void showSignUpSuccess(String message);
    void showSignUpFailure(String message);
    void navigateToManageScreen();
    String getName();
    String getEmail();
    String getPassword();
    String getDob();
    String getNotes();
}
