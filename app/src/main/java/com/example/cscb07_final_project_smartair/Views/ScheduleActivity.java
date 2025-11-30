package com.example.cscb07_final_project_smartair.Views;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.example.cscb07_final_project_smartair.DataObjects.ScheduleEntry;
import com.example.cscb07_final_project_smartair.Presenters.SchedulePresenter;
import com.example.cscb07_final_project_smartair.R;

import java.util.List;

public class ScheduleActivity extends BaseActivity implements ScheduleView {
    private SchedulePresenter presenter;

    private Spinner spinnerChild;
    private Spinner spinnerDay;
    private LinearLayout scheduleContainer;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller_schedule);

        presenter = new SchedulePresenter(this);

        spinnerChild = findViewById(R.id.SDspinnerChild);
        spinnerDay = findViewById(R.id.SDspinnerDay);
        scheduleContainer = findViewById(R.id.SDscheduleListContainer);

        findViewById(R.id.SDbtnAddEntry).setOnClickListener(v -> presenter.startAddEntry());
        findViewById(R.id.SDbtnBack).setOnClickListener(v -> navigateBackHome());

        presenter.loadChildren();
    }

    @Override
    public void displayChildren(List<String> names) {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerChild.setAdapter(adapter);

        spinnerChild.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                presenter.onChildSelected(pos);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        String[] days = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
        ArrayAdapter<String> dayAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, days);

        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(dayAdapter);

        spinnerDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                presenter.onDaySelected(days[pos]);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    public void displayScheduleForDay(List<ScheduleEntry> entries) {
        scheduleContainer.removeAllViews();

        for (ScheduleEntry entry : entries) {
            View card = getLayoutInflater().inflate(R.layout.item_schedule_entry, null);

            ((TextView) card.findViewById(R.id.tvScheduleTime)).setText(entry.time);
            ((TextView) card.findViewById(R.id.tvScheduleDose)).setText("Dose: " + entry.doseAmount);
            ((TextView) card.findViewById(R.id.tvScheduleNote))
                    .setText(entry.note == null ? "" : entry.note);

            card.findViewById(R.id.btnEditSchedule).setOnClickListener(v -> presenter.startEditEntry(entry));
            card.findViewById(R.id.btnDeleteSchedule).setOnClickListener(v -> presenter.deleteEntry(entry));

            scheduleContainer.addView(card);
        }
    }

    @Override
    public void displayEmptyDayMessage() {
        scheduleContainer.removeAllViews();
        TextView tv = new TextView(this);
        tv.setText("No entries for this day.");
        tv.setTextSize(16);
        scheduleContainer.addView(tv);
    }

    @Override
    public void showAddEditDialog(ScheduleEntry existing) {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_edit_schedule_entry);

        EditText time = dialog.findViewById(R.id.etScheduleTime);
        EditText dose = dialog.findViewById(R.id.etScheduleDose);
        EditText note = dialog.findViewById(R.id.etScheduleNote);

        Button save = dialog.findViewById(R.id.btnSaveScheduleEntry);
        Button delete = dialog.findViewById(R.id.btnDeleteScheduleEntry);

        if (existing != null) {
            time.setText(existing.time);
            dose.setText(String.valueOf(existing.doseAmount));
            note.setText(existing.note);

            delete.setVisibility(View.VISIBLE);
            delete.setOnClickListener(v -> presenter.deleteEntry(existing));
        } else {
            delete.setVisibility(View.GONE);
        }

        save.setOnClickListener(v -> presenter.saveScheduleEntry(existing));

        dialog.show();
    }

    @Override
    public void closeDialog() {
        if (dialog != null) dialog.dismiss();
    }

    @Override
    public String getDialogTime() {
        return ((EditText) dialog.findViewById(R.id.etScheduleTime)).getText().toString().trim();
    }

    @Override
    public String getDialogDose() {
        return ((EditText) dialog.findViewById(R.id.etScheduleDose)).getText().toString().trim();
    }

    @Override
    public String getDialogNote() {
        return ((EditText) dialog.findViewById(R.id.etScheduleNote)).getText().toString().trim();
    }
    @Override
    public void showError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSuccess(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void navigateBackHome() {
        startActivity(new Intent(this, MainActivityView.class));
        finish();
    }
}
