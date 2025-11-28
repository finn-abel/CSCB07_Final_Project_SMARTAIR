package com.example.cscb07_final_project_smartair.Views;

import com.example.cscb07_final_project_smartair.Models.Items.ControllerLogEntry;
import com.example.cscb07_final_project_smartair.Models.Items.RescueLogEntry;

import java.util.List;

public interface MedicineLogsView {
    void showRescueLogs(List<RescueLogEntry> logs);
    void showControllerLogs(List<ControllerLogEntry> logs);
    void showSuccess(String msg);
    void showError(String msg);
    void navigateBack();
}
