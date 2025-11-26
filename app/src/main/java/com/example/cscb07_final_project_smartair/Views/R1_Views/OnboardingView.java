package com.example.cscb07_final_project_smartair.Views.R1_Views;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cscb07_final_project_smartair.Presenters.MainActivityPresenter;
import com.example.cscb07_final_project_smartair.R;
import com.example.cscb07_final_project_smartair.Presenters.R1_Presenters.OnboardingPresenter;
import com.google.android.material.button.MaterialButton;

public class OnboardingView extends AppCompatActivity{

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
        // to-do:
        // change this to where you want to navigate!!!!!!!!
        startActivity(new Intent(this, MainActivityPresenter.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }
}

