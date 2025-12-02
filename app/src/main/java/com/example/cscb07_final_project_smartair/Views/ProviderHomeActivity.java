package com.example.cscb07_final_project_smartair.Views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cscb07_final_project_smartair.Presenters.ProviderHomePresenter;
import com.example.cscb07_final_project_smartair.R;

public class ProviderHomeActivity extends BaseActivity implements ProviderHomeView {
    private ProviderHomePresenter presenter;
    private EditText etInviteCode;
    private Button btnSubmitInviteCode;
    private Button btnProviderReport;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_home);

        presenter = new ProviderHomePresenter(this);

        etInviteCode = findViewById(R.id.etInviteCode);
        btnSubmitInviteCode = findViewById(R.id.btnSubmitInviteCode);
        btnProviderReport = findViewById(R.id.btnProviderReport);
        logoutButton = findViewById(R.id.logout);

        btnSubmitInviteCode.setOnClickListener(v -> presenter.onLinkChildClicked());
        btnProviderReport.setOnClickListener(v -> presenter.onProviderReportButtonClicked());
        logoutButton.setOnClickListener(v -> presenter.onLogoutButtonClicked());
    }

    @Override
    public void navigateToRoleSelectionScreen() {
        Intent intent = new Intent(this, RoleLauncherActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public String getInviteCodeInput() {
        return etInviteCode.getText().toString().trim();
    }
    @Override
    public void clearInviteCodeInput() {
        etInviteCode.setText("");
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
