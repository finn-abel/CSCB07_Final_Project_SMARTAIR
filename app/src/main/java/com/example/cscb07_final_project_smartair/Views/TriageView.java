package com.example.cscb07_final_project_smartair.Views;

public interface TriageView {
    void onFormSubmit(boolean speak, boolean lips, boolean chest);
    void showCheckup();
    void showDecision(boolean isRedFlag);

    void showTimerStart();

    void showSteps();

    void closeDialog();

    void callEmergency();
}
