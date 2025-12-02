package com.example.cscb07_final_project_smartair.Views;

import android.content.Context;

import com.example.cscb07_final_project_smartair.Users.ChildSpinnerOption;

import java.util.List;

public interface ParentHomeView {
    public Context getContext();

    void navigateToLoginScreen();

    void navigateToCheckInScreen();

    void navigateToCheckInHistoryScreen();

    void navigateToMedicineLogs();

    void navigateToBadgeSettings();

    void navigateToPEFEntry();

    void navigateToSchedule();

    void navigateToInventory();

    void navigateToProviderReport();

    public void displayChildren(List<ChildSpinnerOption> names);

    void setActiveChild(String id);

    void navigateToManageChildren();
}
