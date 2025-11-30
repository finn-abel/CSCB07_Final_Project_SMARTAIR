package com.example.cscb07_final_project_smartair.Presenters;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cscb07_final_project_smartair.Models.MainActivityModel;
import com.example.cscb07_final_project_smartair.Models.SignUpModel;
import com.example.cscb07_final_project_smartair.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.cscb07_final_project_smartair.Views.MainActivityView;
import com.example.cscb07_final_project_smartair.Views.SignUpView;

public class MainActivityPresenter extends AppCompatActivity {

    private final MainActivityView view;
    private final MainActivityModel model;

    public MainActivityPresenter(MainActivityView view){
        this.view = view;
        this.model = new MainActivityModel();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Firebase test - to delete later

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("test_message");

        myRef.setValue("Test Firebase 3").addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("FirebaseTest", "Data written successfully");
            } else {
                Log.e("FirebaseTest", "Failed to write data", task.getException());
            }
        });

        // End of test
    }


    public void onLogoutButtonClicked() {
        model.signOut();
        view.navigateToLoginScreen();
    }

    public void onPEFButtonClicked(){
        view.navigateToPEFEntry();
    }

    public void onCheckInButtonClicked() {
        view.navigateToCheckInScreen();
    }

    public void onMedicineLogsClicked() {
        view.navigateToMedicineLogs();
    }

    public void onInventoryClicked() {
        view.navigateToInventory();
    }
    public void onCheckInHistoryClicked() {
        view.navigateToCheckInHistoryScreen();
    }
}