package com.example.cscb07_final_project_smartair.Views;

import com.example.cscb07_final_project_smartair.DataObjects.Badge;

import java.util.List;

public interface MainView {
    void navigateToRoleSelectionScreen();
    void navigateToCheckInScreen();
    void navigateToMedicineLogs();
    void navigateToPEFEntry();
    void navigateToCheckInHistoryScreen();
    void displayBadges(List<Badge> badges);
    void setStreaks(int controllerStreak, int techniqueStreak);
    void displayNextDose(String text);
}
