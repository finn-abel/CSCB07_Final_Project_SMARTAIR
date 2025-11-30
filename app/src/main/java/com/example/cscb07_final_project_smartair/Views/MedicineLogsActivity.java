package com.example.cscb07_final_project_smartair.Views;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.cscb07_final_project_smartair.DataObjects.ControllerDose;
import com.example.cscb07_final_project_smartair.DataObjects.RescueDose;
import com.example.cscb07_final_project_smartair.Presenters.MedicineLogsPresenter;
import com.example.cscb07_final_project_smartair.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MedicineLogsActivity extends BaseActivity implements MedicineLogsView {
    private MedicineLogsPresenter presenter;

    private Dialog controllerDialog;
    private Dialog rescueDialog;
    private LinearLayout controllerLogContainer;
    private LinearLayout rescueLogContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_logs);

        presenter = new MedicineLogsPresenter(this);

        controllerLogContainer = findViewById(R.id.recyclerControllerLogs);
        rescueLogContainer = findViewById(R.id.recyclerRescueLogs);

        // Buttons
        Button btnController = findViewById(R.id.btnLogController);
        Button btnRescue = findViewById(R.id.btnLogRescue);
        Button btnBack = findViewById(R.id.btnBackToHome);

        btnController.setOnClickListener(v -> showControllerPopup());
        btnRescue.setOnClickListener(v -> showRescuePopup());
        btnBack.setOnClickListener(v -> navigateToMainActivity());

        presenter.loadLogs(); // fetch last 72 hours
    }

    // popup handling
    @Override
    public void showControllerPopup() {
        controllerDialog = new Dialog(this);
        controllerDialog.setContentView(R.layout.dialog_controller_dose);

        Button submit = controllerDialog.findViewById(R.id.submitControllerDose);
        submit.setOnClickListener(v -> presenter.onLogControllerClicked());

        controllerDialog.show();
    }
    @Override
    public void showRescuePopup() {
        rescueDialog = new Dialog(this);
        rescueDialog.setContentView(R.layout.dialog_rescue_dose);

        Button submit = rescueDialog.findViewById(R.id.submitRescueDose);
        submit.setOnClickListener(v -> presenter.onLogRescueClicked());

        rescueDialog.show();
    }
    @Override
    public void closeControllerPopup() {
        if (controllerDialog != null) controllerDialog.dismiss();
    }
    @Override
    public void closeRescuePopup() {
        if (rescueDialog != null) rescueDialog.dismiss();
    }

    // methods to snag user inputs
    @Override
    public String getControllerDoseAmount() {
        EditText et = controllerDialog.findViewById(R.id.controllerDoseAmount);
        return et.getText().toString().trim();
    }
    @Override
    public String getRescueDoseAmount() {
        EditText et = rescueDialog.findViewById(R.id.rescueDoseAmount);
        return et.getText().toString().trim();
    }
    @Override
    public String getBreathingBefore() {
        EditText et = rescueDialog.findViewById(R.id.breathingBefore);
        return et.getText().toString().trim();
    }
    @Override
    public String getBreathingAfter() {
        EditText et = rescueDialog.findViewById(R.id.breathingAfter);
        return et.getText().toString().trim();
    }
    @Override
    public String getShortnessOfBreath() {
        EditText et = rescueDialog.findViewById(R.id.shortnessOfBreath);
        return et.getText().toString().trim();
    }

    //Log clearing and rendering
    @Override
    public void clearControllerLogs() {
        controllerLogContainer.removeAllViews();
    }
    @Override
    public void clearRescueLogs() {
        rescueLogContainer.removeAllViews();
    }
    @Override
    public void showNoControllerLogs() {
        TextView tv = new TextView(this);
        tv.setText("No controller doses logged in last 72 hours.");
        tv.setPadding(0, 16, 0, 16);
        controllerLogContainer.addView(tv);
    }
    @Override
    public void showNoRescueLogs() {
        TextView tv = new TextView(this);
        tv.setText("No rescue doses logged in last 72 hours.");
        tv.setPadding(0, 16, 0, 16);
        rescueLogContainer.addView(tv);
    }
    @Override
    public void addControllerLog(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setPadding(0, 16, 0, 16);
        controllerLogContainer.addView(tv);
    }
    @Override
    public void addRescueLog(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setPadding(0, 16, 0, 16);
        rescueLogContainer.addView(tv);
    }

    //fills list view thing
    @Override
    public void displayControllerLogs(List<ControllerDose> logs) {
        clearControllerLogs();

        if (logs == null || logs.isEmpty()) {
            showNoControllerLogs();
            return;
        }

        for (ControllerDose log : logs) {
            String time = new SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
                    .format(new Date(log.timestamp));
            String entry = "Dose: " + log.doseAmount + "\n" + "Before: " + log.breathingBefore +
                    "\n" + "After: " + log.breathingAfter + "\n" + "Time: " + time;
            addControllerLog(entry);
        }
    }
    @Override
    public void displayRescueLogs(List<RescueDose> logs) {
        clearRescueLogs();

        if (logs == null || logs.isEmpty()) {
            showNoRescueLogs();
            return;
        }

        for (RescueDose log : logs) {
            String time = new SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
                    .format(new Date(log.timestamp));

            String entry = "Dose: " + log.doseAmount + "\n" +
                            "Before: " + log.breathingBefore + "\n" +
                            "After: " + log.breathingAfter + "\n" +
                            "Shortness of Breath: " + log.shortnessOfBreath + "\n" +
                            "Time: " + time;
            addRescueLog(entry);
        }
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
    public void navigateToMainActivity() {
        startActivity(new Intent(this, MainActivityView.class));
        finish();
    }
}
