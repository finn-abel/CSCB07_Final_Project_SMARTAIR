package com.example.cscb07_final_project_smartair.Views;

import android.os.Bundle;

import android.content.Intent;
import android.widget.Button;

import com.example.cscb07_final_project_smartair.Presenters.MainActivityPresenter;
import com.example.cscb07_final_project_smartair.R;

public class MainActivityView extends BaseActivity implements MainView{
    private MainActivityPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        presenter = new MainActivityPresenter(this);

        Button check_in_button = findViewById(R.id.checkin);
        Button logout_button = findViewById(R.id.logout);
        Button btnLogs = findViewById(R.id.btnMedicineLogs);
        Button btnInventory = findViewById(R.id.btnInventory);
        Button btnPEF = findViewById(R.id.btnPEF);
        Button btnCheckInHistory = findViewById(R.id.btnCheckInHistory);

        Button btnSchedule = findViewById(R.id.btnSchedule);
        Button btnBadgeSettings = findViewById(R.id.btnBadgeSettings);

        btnPEF.setOnClickListener(view -> {
            presenter.onPEFButtonClicked();
        });
        btnBadgeSettings.setOnClickListener(view -> {
            presenter.onBadgeSettingsClicked();
        });
        btnSchedule.setOnClickListener(view -> {
            presenter.onScheduleButtonClicked();
        });


        logout_button.setOnClickListener(view -> {
            presenter.onLogoutButtonClicked();
        });

        check_in_button.setOnClickListener(view -> {
            presenter.onCheckInButtonClicked();
        });
        btnCheckInHistory.setOnClickListener(v -> presenter.onCheckInHistoryClicked());

        btnLogs.setOnClickListener(v -> presenter.onMedicineLogsClicked());
        btnInventory.setOnClickListener(v -> presenter.onInventoryClicked());
    }

    @Override
    public void navigateToLoginScreen(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void navigateToCheckInScreen(){
        startActivity(new Intent(this, CheckInActivity.class));
    }

    @Override
    public void navigateToCheckInHistoryScreen(){
        startActivity(new Intent(this, CheckInHistoryActivity.class));
    }

    @Override
    public void navigateToMedicineLogs() {
        startActivity(new Intent(this, MedicineLogsActivity.class));
    }
    @Override
    public void navigateToBadgeSettings() {
        startActivity(new Intent(this, BadgeSettingsActivity.class));
    }
    @Override
    public void navigateToPEFEntry() {
        startActivity(new Intent(this, PEFActivity.class));
    }
    @Override
    public void navigateToSchedule() {
        startActivity(new Intent(this, ScheduleActivity.class));
    }

    @Override
    public void navigateToInventory() {
        startActivity(new Intent(this, InventoryActivity.class));
    }
}
