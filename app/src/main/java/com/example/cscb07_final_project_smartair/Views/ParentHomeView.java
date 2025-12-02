package com.example.cscb07_final_project_smartair.Views;

import android.content.Context;

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

    void navigateToManageChildren();

    public void displayChildren(List<String> names);

    void setActiveChild(String id);
}
