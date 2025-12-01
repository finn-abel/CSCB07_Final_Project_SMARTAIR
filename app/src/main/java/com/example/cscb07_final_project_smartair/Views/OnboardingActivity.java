package com.example.cscb07_final_project_smartair.Views;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cscb07_final_project_smartair.Presenters.MainActivityPresenter;
import com.example.cscb07_final_project_smartair.R;
import com.example.cscb07_final_project_smartair.Presenters.OnboardingPresenter;
import com.google.android.material.button.MaterialButton;

public class OnboardingActivity extends AppCompatActivity{

    private OnboardingPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        presenter = new OnboardingPresenter(this);

        MaterialButton cta = findViewById(R.id.onboarding_cta);
        cta.setOnClickListener(v -> presenter.onContinueClicked());
    }

    public void navigateToMainScreen() {
        startActivity(new Intent(this, MainActivityPresenter.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }
}

