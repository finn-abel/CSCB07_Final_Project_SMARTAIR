package com.example.cscb07_final_project_smartair.Models;

import com.example.cscb07_final_project_smartair.Repository.R3_Repository;
import com.example.cscb07_final_project_smartair.Repository.RepositoryCallback;
import com.example.cscb07_final_project_smartair.Models.Items.ControllerLogEntry;
import com.example.cscb07_final_project_smartair.Models.Items.RescueLogEntry;

import java.util.List;

public class MedicineLogsModel {
    private final R3_Repository repo;

    public MedicineLogsModel(R3_Repository repo) {
        this.repo = repo;
    }

    public void addRescueLog(String childId, RescueLogEntry entry, RepositoryCallback<Void> cb) {
        repo.addRescueLog(childId, entry, cb);
    }

    public void getRescueLogs(String childId, RepositoryCallback<List<RescueLogEntry>> cb) {
        repo.getRescueLogs(childId, cb);
    }

    public void addControllerLog(String childId, ControllerLogEntry entry, RepositoryCallback<Void> cb) {
        repo.addControllerLog(childId, entry, cb);
    }

    public void getControllerLogs(String childId, RepositoryCallback<List<ControllerLogEntry>> cb) {
        repo.getControllerLogs(childId, cb);
    }

    public void updateInventoryAfterDose(String childId, String medName, int amountUsed, RepositoryCallback<Void> cb) {
        repo.updateInventoryAfterDose(childId, medName, amountUsed, cb);
    }
}

