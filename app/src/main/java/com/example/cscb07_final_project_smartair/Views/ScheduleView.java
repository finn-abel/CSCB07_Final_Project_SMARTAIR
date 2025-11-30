package com.example.cscb07_final_project_smartair.Views;

import com.example.cscb07_final_project_smartair.DataObjects.ScheduleEntry;
import java.util.List;

public interface ScheduleView {
    void displayChildren(List<String> childNames);
    void displayScheduleForDay(List<ScheduleEntry> entries);
    void displayEmptyDayMessage();

    void showAddEditDialog(ScheduleEntry entry);
    void closeDialog();

    String getDialogTime();
    String getDialogDose();
    String getDialogNote();

    void showError(String msg);
    void showSuccess(String msg);

    void navigateBackHome();
}
