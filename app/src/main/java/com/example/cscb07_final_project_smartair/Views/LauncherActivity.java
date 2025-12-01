package com.example.cscb07_final_project_smartair.Views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cscb07_final_project_smartair.Presenters.LauncherPresenter;

/**
 * This is the first activity that launches. Its sole purpose is to check
 * the user's login status and navigate to the appropriate screen.
 * It implements the LauncherView interface and contains no logic.
 */
public class LauncherActivity extends AppCompatActivity implements LauncherView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LauncherPresenter presenter = new LauncherPresenter(this);
        presenter.decideNextActivity();
    }



    @Override
    public void navigateToMainScreen() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String role = prefs.getString("USER_ROLE", "");

        Class<?> activityClass;

        if(role.equals("PARENT")) { //redirect to parent home
            activityClass = ParentHomeActivity.class;
        } else { //redirect to child home
            activityClass = MainActivityView.class;
        }

        Intent MainIntent = new Intent(LauncherActivity.this, activityClass);

        if (getIntent().getBooleanExtra("SHOW_TRIAGE_RECHECK", false)) {
            MainIntent.putExtra("SHOW_TRIAGE_RECHECK", true);

            String childID = getIntent().getStringExtra("CHILD_ID");
            if (childID != null) {
                MainIntent.putExtra("CHILD_ID", childID); //add child if there
            }

        } //check for triage recheck

        startActivity(MainIntent);
        finish();
    }

    @Override
    public void navigateToRoleLauncherScreen() {
        startActivity(new Intent(this, RoleLauncherActivity.class));
        finish();
    }

    @Override
    public void navigateToRoleSelectionScreen() {
        startActivity(new Intent(this, RoleSelectionActivity.class));
        finish();
    }

}


