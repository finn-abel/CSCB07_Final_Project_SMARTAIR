package com.example.cscb07_final_project_smartair.Presenters;

import android.content.Context;

import com.example.cscb07_final_project_smartair.Models.MedicineLogsModel;
import com.example.cscb07_final_project_smartair.Views.MedicineLogsView;

public class MedicineLogsPresenter {
    private final MedicineLogsView view;
    private final MedicineLogsModel model;

    public MedicineLogsPresenter(MedicineLogsView view) {
        this.view = view;
        this.model = new MedicineLogsModel(this, (Context) view);
    }

    public void loadChildren() {
        model.loadChildren();
    }

    public void onChildrenLoaded(java.util.List<String> ids, java.util.List<String> names) {
        view.showChildren(ids, names);
    }
    public void loadLogsForChild(String childId) {
        model.getControllerDoses(childId);
        model.getRescueDoses(childId);
    }

    public void onLogControllerClicked(String childId) {
        String doseStr = view.getControllerDoseAmount();
        if (doseStr.isEmpty()) {
            view.showError("Please enter a controller dose amount.");
            return;
        }

        int dose;
        try {
            dose = Integer.parseInt(doseStr);
        } catch (Exception e) {
            view.showError("Dose amount must be a number.");
            return;
        }

        model.logController(childId, dose, view.getControllerBreathingBefore(), view.getControllerBreathingAfter());
        loadLogsForChild(childId);
    }

    public void onControllerLogSuccess() {
        view.closeControllerPopup();
        view.showSuccess("Controller dose logged successfully.");
    }

    public void onLogRescueClicked(String childId) {
        String doseStr = view.getRescueDoseAmount();
        if (doseStr.isEmpty()) {
            view.showError("Please enter a rescue dose amount.");
            return;
        }

        int dose;
        try {
            dose = Integer.parseInt(doseStr);
        } catch (Exception e) {
            view.showError("Dose amount must be a number.");
            return;
        }

        model.logRescue(childId, dose, view.getRescueBreathingBefore(), view.getRescueBreathingAfter(), view.getRescueShortnessOfBreath());
        loadLogsForChild(childId);
    }

    public void onRescueLogSuccess() {
        view.closeRescuePopup();
        view.showSuccess("Rescue dose logged successfully.");
    }

    public void onControllerLogsLoaded(java.util.List<com.example.cscb07_final_project_smartair.DataObjects.ControllerDose> list) {
        view.displayControllerLogs(list);
    }

    public void onRescueLogsLoaded(java.util.List<com.example.cscb07_final_project_smartair.DataObjects.RescueDose> list) {
        view.displayRescueLogs(list);
    }
    public void onFailure(String msg) {
        view.showError(msg);
    }
}
