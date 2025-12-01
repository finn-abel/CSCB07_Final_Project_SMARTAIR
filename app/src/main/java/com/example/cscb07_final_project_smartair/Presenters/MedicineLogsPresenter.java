package com.example.cscb07_final_project_smartair.Presenters;

import com.example.cscb07_final_project_smartair.Models.MedicineLogsModel;
import com.example.cscb07_final_project_smartair.Views.MedicineLogsView;

public class MedicineLogsPresenter {
    private final MedicineLogsView view;
    private final MedicineLogsModel model;

    public MedicineLogsPresenter(MedicineLogsView view) {
        this.view = view;
        this.model = new MedicineLogsModel(this);
    }

    public void loadLogs() {
        model.getControllerDoses();
        model.getRescueDoses();
    }

    public void onLogControllerClicked() {
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

        int before = view.getControllerBreathingBefore();
        int after = view.getControllerBreathingAfter();

        model.logController(dose, before, after);
    }

    public void onControllerLogSuccess() {
        view.closeControllerPopup();
        view.showSuccess("Controller dose logged successfully.");
        loadLogs();
    }

    public void onFailure(String msg) {
        view.showError(msg);
    }
    public void onLogRescueClicked() {
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

        int before = view.getRescueBreathingBefore();
        int after = view.getRescueBreathingAfter();
        int sob = view.getRescueShortnessOfBreath();

        model.logRescue(dose, before, after, sob);
    }

    public void onRescueLogSuccess() {
        view.closeRescuePopup();
        view.showSuccess("Rescue dose logged successfully.");
        loadLogs();
    }

    public void onControllerLogsLoaded(java.util.List<com.example.cscb07_final_project_smartair.DataObjects.ControllerDose> list) {
        view.displayControllerLogs(list);
    }

    public void onRescueLogsLoaded(java.util.List<com.example.cscb07_final_project_smartair.DataObjects.RescueDose> list) {
        view.displayRescueLogs(list);
    }
}
