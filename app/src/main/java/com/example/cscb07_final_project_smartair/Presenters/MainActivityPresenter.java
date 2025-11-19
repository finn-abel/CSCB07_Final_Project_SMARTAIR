package com.example.cscb07_final_project_smartair.Presenters;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cscb07_final_project_smartair.Models.MainActivityModel;
import com.example.cscb07_final_project_smartair.Models.SignUpModel;
import com.example.cscb07_final_project_smartair.R;
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
    }


    public void onLogoutButtonClicked() {
        model.signOut();
        view.navigateToLoginScreen();
    }
}