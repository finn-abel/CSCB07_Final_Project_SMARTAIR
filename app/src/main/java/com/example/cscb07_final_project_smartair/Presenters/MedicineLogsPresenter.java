package com.example.cscb07_final_project_smartair.Presenters;

import com.example.cscb07_final_project_smartair.DataObjects.ControllerDose;
import com.example.cscb07_final_project_smartair.DataObjects.RescueDose;
import com.example.cscb07_final_project_smartair.Models.MedicineLogsModel;
import com.example.cscb07_final_project_smartair.Views.MedicineLogsView;

import java.util.List;

public class MedicineLogsPresenter {
    private final MedicineLogsView view;
    private final MedicineLogsModel model;

    public MedicineLogsPresenter(MedicineLogsView view) {
        this.view = view;
        this.model = new MedicineLogsModel(this);
    }

    public void onLogControllerClicked() {
        String doseText = view.getControllerDoseAmount();

        if (doseText.isEmpty()) {
            view.showError("Dose amount cannot be empty.");
            return;
        }

        int dose = Integer.parseInt(doseText);
        int before = view.getControllerBreathingBefore();
        int after = view.getControllerBreathingAfter();

        model.logController(dose, before, after);
    }

    public void onLogRescueClicked() {
        String doseText = view.getRescueDoseAmount();

        if (doseText.isEmpty()) {
            view.showError("Dose amount cannot be empty.");
            return;
        }

        int dose = Integer.parseInt(doseText);
        int before = view.getRescueBreathingBefore();
        int after = view.getRescueBreathingAfter();
        int sob = view.getRescueShortnessOfBreath();

        model.logRescue(dose, before, after, sob);
    }

    public void onControllerLogSuccess() {
        view.showSuccess("Controller dose logged!");
        view.closeControllerPopup();
        model.getControllerDoses();
    }

    public void onRescueLogSuccess() {
        view.showSuccess("Rescue dose logged!");
        view.closeRescuePopup();
        model.getRescueDoses();
    }

    public void loadLogs() {
        model.getControllerDoses();
        model.getRescueDoses();
    }

    public void onControllerLogsLoaded(List<ControllerDose> logs) {
        view.displayControllerLogs(logs);
    }

    public void onRescueLogsLoaded(List<RescueDose> logs) {
        view.displayRescueLogs(logs);
    }

    public void onFailure(String msg) {
        view.showError(msg);
    }
}
