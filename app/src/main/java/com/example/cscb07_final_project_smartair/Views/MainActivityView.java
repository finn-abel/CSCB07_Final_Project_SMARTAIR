package com.example.cscb07_final_project_smartair.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.cscb07_final_project_smartair.Presenters.LoginPresenter;
import com.example.cscb07_final_project_smartair.Presenters.MainActivityPresenter;
import com.example.cscb07_final_project_smartair.R;

public class MainActivityView extends AppCompatActivity implements MainView{
    private MainActivityPresenter presenter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button logout_button = findViewById(R.id.logout);

        presenter = new MainActivityPresenter(this);

        logout_button.setOnClickListener(view -> {
            presenter.onLogoutButtonClicked();
        });
    }

    @Override
    public void navigateToLoginScreen(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


}
