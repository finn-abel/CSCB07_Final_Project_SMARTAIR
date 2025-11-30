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
            view.showError("Dose amount cannot be empty");
            return;
        }

        try {
            int dose = Integer.parseInt(doseText);
            model.logController(dose);
        } catch (Exception e) {
            view.showError("Invalid dose amount");
        }
    }

    public void onControllerLogSuccess() {
        view.showSuccess("Controller dose logged!");
        view.closeControllerPopup();
        model.getControllerDoses();
    }

    public void onLogRescueClicked() {
        String doseText = view.getRescueDoseAmount();
        String beforeText = view.getBreathingBefore();
        String afterText = view.getBreathingAfter();
        String sobText = view.getShortnessOfBreath();

        if (doseText.isEmpty() || beforeText.isEmpty() || afterText.isEmpty() || sobText.isEmpty()) {
            view.showError("All rescue fields must be filled");
            return;
        }

        try {
            int dose = Integer.parseInt(doseText);
            int before = Integer.parseInt(beforeText);
            int after = Integer.parseInt(afterText);
            int sob = Integer.parseInt(sobText);

            model.logRescue(dose, before, after, sob);
        } catch (Exception e) {
            view.showError("Invalid rescue input values");
        }
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
