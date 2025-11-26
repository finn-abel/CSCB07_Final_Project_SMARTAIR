package com.example.cscb07_final_project_smartair.Views;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cscb07_final_project_smartair.Presenters.LauncherPresenter;

/**
 * This is the first activity that launches. Its sole purpose is to check
 * the user's login status and navigate to the appropriate screen.
 * It implements the LauncherView interface and contains no logic.
 */
public class LauncherActivity extends BaseActivity implements LauncherView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LauncherPresenter presenter = new LauncherPresenter(this);
        presenter.decideNextActivity();
    }


    @Override
    public void navigateToLoginScreen() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public void navigateToMainScreen() {
        startActivity(new Intent(this, MainActivityView.class));
        finish();
    }
}


