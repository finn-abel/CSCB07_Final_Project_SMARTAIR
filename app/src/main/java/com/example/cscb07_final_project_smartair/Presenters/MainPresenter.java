package com.example.cscb07_final_project_smartair.Presenters;

import androidx.appcompat.app.AppCompatActivity;
import com.example.cscb07_final_project_smartair.Views.MainView;
public class MainPresenter extends AppCompatActivity {
    private final MainView view;
    public MainPresenter(MainView view) {
        this.view = view;
    }

    public void onMedicineLogsClicked() {
        view.navigateToMedicineLogs();
    }

    public void onInventoryClicked() {
        view.navigateToInventory();
    }
}