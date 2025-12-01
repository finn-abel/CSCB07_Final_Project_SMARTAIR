package com.example.cscb07_final_project_smartair.Views;

import com.example.cscb07_final_project_smartair.DataObjects.BadgeThresholds;
import com.example.cscb07_final_project_smartair.DataObjects.Badge;

import java.util.List;

public interface BadgeSettingsView {
    void populateChildList(List<String> childNames);
    void displayThresholds(BadgeThresholds thresholds);
    void displayBadges(List<Badge> badges);

    String getPerfectWeekInput();
    String getTechniqueInput();
    String getLowRescueInput();

    void showError(String msg);
    void showSuccess(String msg);

    void navigateHome();
}
