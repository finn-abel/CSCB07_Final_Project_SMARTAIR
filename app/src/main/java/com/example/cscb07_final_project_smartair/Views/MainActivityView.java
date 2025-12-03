package com.example.cscb07_final_project_smartair.Views;

import android.graphics.Color;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.cscb07_final_project_smartair.DataObjects.Badge;
import com.example.cscb07_final_project_smartair.Presenters.MainActivityPresenter;
import com.example.cscb07_final_project_smartair.R;


import java.util.ArrayList;
import java.util.List;

public class MainActivityView extends BaseActivity implements MainView {
    private MainActivityPresenter presenter;

    private LinearLayout badgeContainer;
    private TextView tvControllerStreak;
    private TextView tvTechniqueStreak;
    private TextView tvNextDose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        presenter = new MainActivityPresenter(this);

        Button check_in_button = findViewById(R.id.checkin);
        Button logout_button = findViewById(R.id.logout);
        Button btnMedicineLogs = findViewById(R.id.btnMedicineLogs);
        Button btnPEF = findViewById(R.id.btnPEF);
        Button btnCheckInHistory = findViewById(R.id.btnCheckInHistory);

        check_in_button.setOnClickListener(v -> presenter.onCheckInButtonClicked());
        logout_button.setOnClickListener(v -> presenter.onLogoutButtonClicked());
        btnMedicineLogs.setOnClickListener(v -> presenter.onMedicineLogsClicked());
        btnPEF.setOnClickListener(v -> presenter.onPEFButtonClicked());
        btnCheckInHistory.setOnClickListener(v -> presenter.onCheckInHistoryClicked());

        badgeContainer = findViewById(R.id.badgeContainer);
        tvControllerStreak = findViewById(R.id.tvControllerStreak);
        tvTechniqueStreak = findViewById(R.id.tvTechniqueStreak);
        tvNextDose = findViewById(R.id.tvNextDose);

        presenter.loadMainPageData();
    }


    @Override
    public void navigateToRoleSelectionScreen() {
        startActivity(new Intent(this, RoleLauncherActivity.class));
        finish();
    }

    @Override
    public void navigateToCheckInScreen() {
        startActivity(new Intent(this, CheckInActivity.class));
    }

    @Override
    public void navigateToCheckInHistoryScreen() {
        startActivity(new Intent(this, CheckInHistoryActivity.class));
    }

    @Override
    public void navigateToMedicineLogs() {
        startActivity(new Intent(this, MedicineLogsActivity.class));
    }

    @Override
    public void navigateToPEFEntry() {
        startActivity(new Intent(this, PEFActivity.class));
    }

    @Override
    public void displayBadges(List<Badge> badges) {
        badgeContainer.removeAllViews();

        if (badges.isEmpty()) {
            TextView tv = new TextView(this);
            tv.setText("No badges earned yet.");
            tv.setPadding(12, 12, 12, 12);
            badgeContainer.addView(tv);
            return;
        }

        for (Badge b : badges) {
            View card = getLayoutInflater().inflate(R.layout.item_badge_card, badgeContainer, false);

            ImageView icon = card.findViewById(R.id.BCimgBadgeIcon);
            TextView name = card.findViewById(R.id.BCbadgeName);
            TextView desc = card.findViewById(R.id.BCbadgeDesc);

            icon.setImageResource(b.iconRes);
            name.setText(b.title);
            desc.setText(b.description);

            badgeContainer.addView(card);
        }
    }

    @Override
    public void setStreaks(int controllerStreak, int techniqueStreak) {
        tvControllerStreak.setText("Controller Streak: " + controllerStreak + " days");
        tvTechniqueStreak.setText("Technique Streak: " + techniqueStreak + " days");
    }

    @Override
    public void displayNextDose(String text) {
        tvNextDose.setText(text);
    }

}
