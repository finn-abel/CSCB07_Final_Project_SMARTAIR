package com.example.cscb07_final_project_smartair.Presenters;

import com.example.cscb07_final_project_smartair.DataObjects.ScheduleEntry;
import com.example.cscb07_final_project_smartair.Models.ScheduleModel;
import com.example.cscb07_final_project_smartair.Views.ScheduleView;

import java.util.List;

public class SchedulePresenter {
    private ScheduleView view;
    private ScheduleModel model;

    private String selectedChildId;
    private String selectedDay;
    private List<String> childIds;

    public SchedulePresenter(ScheduleView view) {
        this.view = view;
        this.model = new ScheduleModel(this);
    }

    public void loadChildren() {
        model.fetchChildren();
    }

    public void onChildrenLoaded(List<String> names, List<String> ids) {
        this.childIds = ids;
        view.displayChildren(names);

        if (!ids.isEmpty())
        {
            selectedChildId = ids.get(0);
            selectedDay = "Monday";
            model.fetchScheduleDay(selectedChildId, selectedDay);
        }
    }

    public void onChildSelected(int index) {
        selectedChildId = childIds.get(index);
        selectedDay = "Monday";
        model.fetchScheduleDay(selectedChildId, selectedDay);
    }

    public void onDaySelected(String day) {
        selectedDay = day;
        model.fetchScheduleDay(selectedChildId, day);
    }

    public void onDayScheduleLoaded(List<ScheduleEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            view.displayEmptyDayMessage();
        }
        else
        {
            view.displayScheduleForDay(entries);
        }
    }

    public void startAddEntry() {
        view.showAddEditDialog(null);
    }

    public void startEditEntry(ScheduleEntry entry) {
        view.showAddEditDialog(entry);
    }

    public void saveScheduleEntry(ScheduleEntry existing) {
        String time = view.getDialogTime();
        String doseText = view.getDialogDose();
        String note = view.getDialogNote();

        if (time.isEmpty() || doseText.isEmpty())
        {
            view.showError("Time and dose are required.");
            return;
        }

        int dose;
        try
        {
            dose = Integer.parseInt(doseText);
        }
        catch (Exception e)
        {
            view.showError("Invalid dose value.");
            return;
        }

        ScheduleEntry entry = new ScheduleEntry(time, dose, note);
        model.saveScheduleEntry(selectedChildId, selectedDay, entry, existing);
    }

    public void onEntrySaved() {
        view.showSuccess("Saved.");
        view.closeDialog();
        model.fetchScheduleDay(selectedChildId, selectedDay);
    }

    public void deleteEntry(ScheduleEntry entry) {
        model.deleteScheduleEntry(selectedChildId, selectedDay, entry);
    }

    public void onEntryDeleted() {
        view.showSuccess("Deleted.");
        view.closeDialog();
        model.fetchScheduleDay(selectedChildId, selectedDay);
    }

    public void onFailure(String msg) {
        view.showError(msg);
    }
}
