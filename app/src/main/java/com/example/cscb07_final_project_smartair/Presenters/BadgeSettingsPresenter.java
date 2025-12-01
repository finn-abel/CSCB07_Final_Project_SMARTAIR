package com.example.cscb07_final_project_smartair.Presenters;

import com.example.cscb07_final_project_smartair.DataObjects.Badge;
import com.example.cscb07_final_project_smartair.DataObjects.BadgeThresholds;
import com.example.cscb07_final_project_smartair.Models.BadgeSettingsModel;
import com.example.cscb07_final_project_smartair.Views.BadgeSettingsView;

import java.util.List;

public class BadgeSettingsPresenter {
    private final BadgeSettingsView view;
    private final BadgeSettingsModel model;

    private String selectedChildID;

    public BadgeSettingsPresenter(BadgeSettingsView view) {
        this.view = view;
        this.model = new BadgeSettingsModel(this);

        model.loadChildren();
    }

    public void onChildrenLoaded(List<String> childIDs, List<String> childNames) {
        view.populateChildList(childNames, childIDs);

        if (!childIDs.isEmpty()) {
            selectedChildID = childIDs.get(0);
            model.loadThresholds(selectedChildID);
            model.loadBadges(selectedChildID);
        }
    }

    public void onChildSelected(String childID) {
        selectedChildID = childID;
        model.loadThresholds(childID);
        model.loadBadges(childID);
    }

    public void onThresholdsLoaded(BadgeThresholds t) {
        view.displayThresholds(t);
    }

    public void onBadgesLoaded(List<Badge> badges) {
        view.displayBadges(badges);
    }

    public void onSaveClicked() {
        if (selectedChildID == null)
        {
            view.showError("No child selected");
            return;
        }

        try
        {
            int perfectWeek = Integer.parseInt(view.getPerfectWeekInput());
            int technique = Integer.parseInt(view.getTechniqueInput());
            int lowRescue = Integer.parseInt(view.getLowRescueInput());

            BadgeThresholds newT = new BadgeThresholds(perfectWeek, technique, lowRescue);

            model.saveThresholds(selectedChildID, newT);
        } catch (Exception e)
        {
            view.showError("All values must be valid numbers.");
        }
    }

    public void onSaveSuccess() {
        view.showSuccess("Badge settings saved!");
    }

    public void onFailure(String msg) {
        view.showError(msg);
    }
}
