package com.example.cscb07_final_project_smartair.Views;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.cscb07_final_project_smartair.R;
import com.example.cscb07_final_project_smartair.Presenters.MainPresenter;

public class MainActivity extends AppCompatActivity implements MainView{
    private MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        presenter = new MainPresenter(this);

        Button btnLogs = findViewById(R.id.btnMedicineLogs);
        Button btnInventory = findViewById(R.id.btnInventory);

        btnLogs.setOnClickListener(v -> presenter.onMedicineLogsClicked());
        btnInventory.setOnClickListener(v -> presenter.onInventoryClicked());
    }


    @Override
    public void navigateToMedicineLogs() {
        startActivity(new Intent(this, MedicineLogsActivity.class));
    }

    @Override
    public void navigateToInventory() {
        startActivity(new Intent(this, InventoryActivity.class));
    }
}
