package com.example.cscb07_final_project_smartair.Views;

import com.example.cscb07_final_project_smartair.DataObjects.ControllerDose;
import com.example.cscb07_final_project_smartair.DataObjects.RescueDose;

import java.util.List;

public interface MedicineLogsView {
    String getControllerDoseAmount();
    String getRescueDoseAmount();

    public int getControllerBreathingBefore();
    public int getControllerBreathingAfter();
    public int getRescueBreathingBefore();
    public int getRescueBreathingAfter();
    public int getRescueShortnessOfBreath();

    void showControllerPopup();
    void showRescuePopup();
    void closeControllerPopup();
    void closeRescuePopup();

    void clearRescueLogs();
    void clearControllerLogs();
    void addRescueLog(String text);
    void addControllerLog(String text);
    void showNoRescueLogs();
    void showNoControllerLogs();

    void displayRescueLogs(List<RescueDose> logs);
    void displayControllerLogs(List<ControllerDose> logs);

    void showError(String msg);
    void showSuccess(String msg);
    void navigateToMainActivity();
}
