package com.example.cscb07_final_project_smartair.Presenters;

import com.example.cscb07_final_project_smartair.Models.Items.ControllerLogEntry;
import com.example.cscb07_final_project_smartair.Models.Items.RescueLogEntry;
import com.example.cscb07_final_project_smartair.Models.MedicineLogsModel;
import com.example.cscb07_final_project_smartair.Views.MedicineLogsView;
import com.example.cscb07_final_project_smartair.Repository.R3_Repository;
import com.example.cscb07_final_project_smartair.Repository.RepositoryCallback;

import java.util.List;

public class MedicineLogsPresenter {

    private final MedicineLogsView view;
    private final MedicineLogsModel model;

    public MedicineLogsPresenter(MedicineLogsView view, R3_Repository repo) {
        this.view = view;
        this.model = new MedicineLogsModel(repo);
    }
    public void onBackClicked() {
        view.navigateBack();
    }
    public void loadLogs(String childId) {

        model.getRescueLogs(childId, new RepositoryCallback<List<RescueLogEntry>>() {
            @Override
            public void onSuccess(List<RescueLogEntry> result) {
                view.showRescueLogs(result);
            }
            @Override
            public void onFailure(Exception e) {
                view.showError("Failed to load rescue logs");
            }
        });

        model.getControllerLogs(childId, new RepositoryCallback<List<ControllerLogEntry>>() {
            @Override
            public void onSuccess(List<ControllerLogEntry> result) {
                view.showControllerLogs(result);
            }
            @Override
            public void onFailure(Exception e) {
                view.showError("Failed to load controller logs");
            }
        });
    }

    public void addRescueLog(String childId, int doseCount, boolean betterAfter) {

        RescueLogEntry entry = new RescueLogEntry(
                System.currentTimeMillis(), doseCount, betterAfter
        );

        model.addRescueLog(childId, entry, new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                view.showSuccess("Rescue log saved!");
                loadLogs(childId);
            }
            @Override
            public void onFailure(Exception e) {
                view.showError("Failed to save rescue log");
            }
        });
    }

    public void addControllerLog(String childId, int doseCount) {

        ControllerLogEntry entry = new ControllerLogEntry(
                System.currentTimeMillis(), doseCount
        );

        model.addControllerLog(childId, entry, new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                view.showSuccess("Controller log saved!");
                loadLogs(childId);
            }
            @Override
            public void onFailure(Exception e) {
                view.showError("Failed to save controller log");
            }
        });
    }
}

