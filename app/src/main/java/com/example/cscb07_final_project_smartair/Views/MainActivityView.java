package com.example.cscb07_final_project_smartair.Views;

import android.os.Bundle;

import android.content.Intent;
import android.widget.Button;

import com.example.cscb07_final_project_smartair.Helpers.ProviderReportGenerator;
import com.example.cscb07_final_project_smartair.Presenters.MainActivityPresenter;
import com.example.cscb07_final_project_smartair.R;

public class MainActivityView extends BaseParentActivity implements MainView{ // TO CHANGE; CHANGED TO PARENTACTIVITY
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
        Button btnProviderReport = findViewById(R.id.btnProviderReport);

        btnPEF.setOnClickListener(view -> {
            presenter.onPEFButtonClicked();
        });


        logout_button.setOnClickListener(view -> {
            presenter.onLogoutButtonClicked();
        });

        check_in_button.setOnClickListener(view -> {
            presenter.onCheckInButtonClicked();
        });

        btnLogs.setOnClickListener(v -> presenter.onMedicineLogsClicked());
        btnInventory.setOnClickListener(v -> presenter.onInventoryClicked());

        btnProviderReport.setOnClickListener(view -> {
            presenter.onProviderReportClicked();
        });
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
    public void navigateToMedicineLogs() {
        startActivity(new Intent(this, MedicineLogsActivity.class));
    }
    @Override
    public void navigateToPEFEntry() {
        startActivity(new Intent(this, PEFActivity.class));
    }

    @Override
    public void navigateToInventory() {
        startActivity(new Intent(this, InventoryActivity.class));
    }

    public void navigateToProviderReport() {
        startActivity(new Intent(this, ProviderReportSelectionActivity.class));
    }
}
