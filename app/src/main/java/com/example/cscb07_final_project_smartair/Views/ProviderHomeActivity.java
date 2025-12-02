package com.example.cscb07_final_project_smartair.Views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.cscb07_final_project_smartair.Presenters.ProviderHomePresenter;
import com.example.cscb07_final_project_smartair.R;

public class ProviderHomeActivity extends BaseActivity implements ProviderHomeView{
    private ProviderHomePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_home);

        presenter = new ProviderHomePresenter(this);

        Button btnProviderReport = findViewById(R.id.btnProviderReport);
        Button logout_button = findViewById(R.id.logout);

        btnProviderReport.setOnClickListener(v -> presenter.onProviderReportButtonClicked());
        logout_button.setOnClickListener(v -> presenter.onLogoutButtonClicked());
    }

    @Override
    public void navigateToLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }




}
