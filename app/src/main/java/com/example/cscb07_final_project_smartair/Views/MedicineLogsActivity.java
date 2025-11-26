package com.example.cscb07_final_project_smartair.Views;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cscb07_final_project_smartair.R;
import com.example.cscb07_final_project_smartair.Views.MedicineLogsView;
import com.example.cscb07_final_project_smartair.Models.Items.ControllerLogEntry;
import com.example.cscb07_final_project_smartair.Models.Items.RescueLogEntry;
import com.example.cscb07_final_project_smartair.Presenters.MedicineLogsPresenter;
import com.example.cscb07_final_project_smartair.Repository.R3_FirebaseRepository;

import java.util.List;

public class MedicineLogsActivity extends AppCompatActivity implements MedicineLogsView {

    private MedicineLogsPresenter presenter;
    private final String childId = "child_001"; // TODO: replace with auth childId

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_logs);

        presenter = new MedicineLogsPresenter(this, new R3_FirebaseRepository());

        presenter.loadLogs(childId);

        EditText doseInput = findViewById(R.id.doseInput);
        Switch betterAfterSwitch = findViewById(R.id.betterAfterSwitch);
        Button logRescueBtn = findViewById(R.id.logRescueBtn);
        Button logControllerBtn = findViewById(R.id.logControllerBtn);

        logRescueBtn.setOnClickListener(v -> {
            int dose = Integer.parseInt(doseInput.getText().toString());
            boolean betterAfter = betterAfterSwitch.isChecked();
            presenter.addRescueLog(childId, dose, betterAfter);
        });

        logControllerBtn.setOnClickListener(v -> {
            int dose = Integer.parseInt(doseInput.getText().toString());
            presenter.addControllerLog(childId, dose);
        });
        findViewById(R.id.backButton).setOnClickListener(v ->
                presenter.onBackClicked()
        );
    }
    public void navigateBack() {
        finish();
    }
    @Override
    public void showRescueLogs(List<RescueLogEntry> logs) {
        // TODO: get logs
    }

    @Override
    public void showControllerLogs(List<ControllerLogEntry> logs) {
        // TODO: get logs
    }

    @Override
    public void showSuccess(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}

