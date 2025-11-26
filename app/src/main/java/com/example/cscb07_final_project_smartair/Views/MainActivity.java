package com.example.cscb07_final_project_smartair.Views;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.cscb07_final_project_smartair.Presenters.MainActivityPresenter;
import com.example.cscb07_final_project_smartair.R;
import com.example.cscb07_final_project_smartair.Presenters.MainPresenter;

public class MainActivity {
//    private MainActivityPresenter presenter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        presenter = new MainActivityPresenter(this);
//
//        Button check_in_button = findViewById(R.id.checkin);
//        Button logout_button = findViewById(R.id.logout);
//        Button btnLogs = findViewById(R.id.btnMedicineLogs);
//        Button btnInventory = findViewById(R.id.btnInventory);
//
//
//        logout_button.setOnClickListener(view -> {
//            presenter.onLogoutButtonClicked();
//        });
//
//        check_in_button.setOnClickListener(view -> {
//            presenter.onCheckInButtonClicked();
//        });
//
//        btnLogs.setOnClickListener(v -> presenter.onMedicineLogsClicked());
//        btnInventory.setOnClickListener(v -> presenter.onInventoryClicked());
//    }
//
//
//    @Override
//    public void navigateToMedicineLogs() {
//        startActivity(new Intent(this, MedicineLogsActivity.class));
//    }
//
//    @Override
//    public void navigateToInventory() {
//        startActivity(new Intent(this, InventoryActivity.class));
//    }
//
//    @Override
//    public void navigateToLoginScreen(){
//        Intent intent = new Intent(this, LoginActivity.class);
//        startActivity(intent);
//        finish();
//    }
//
//    @Override
//    public void navigateToCheckInScreen(){
//        startActivity(new Intent(this, CheckInActivity.class));
//    }
}
