package com.example.cscb07_final_project_smartair.Views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;

import com.example.cscb07_final_project_smartair.DataObjects.Badge;
import com.example.cscb07_final_project_smartair.DataObjects.BadgeThresholds;
import com.example.cscb07_final_project_smartair.Presenters.BadgeSettingsPresenter;
import com.example.cscb07_final_project_smartair.R;

import java.util.List;

public class BadgeSettingsActivity extends BaseParentActivity implements BadgeSettingsView {
    private BadgeSettingsPresenter presenter;

    private Spinner spinnerChild;
    private EditText etPerfectWeek, etTechnique, etLowRescue;
    private LinearLayout badgeContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badge_settings);

        presenter = new BadgeSettingsPresenter(this);

        spinnerChild = findViewById(R.id.BSspinnerChild);
        etPerfectWeek = findViewById(R.id.BSperfectWeekThreshold);
        etTechnique = findViewById(R.id.BStechniqueThreshold);
        etLowRescue = findViewById(R.id.BSlowRescueThreshold);
        badgeContainer = findViewById(R.id.BSbadgeListContainer);

        findViewById(R.id.BSbtnSave).setOnClickListener(v -> presenter.onSaveClicked());
    }

    @Override
    public void populateChildList(List<String> childNames, List<String> childIDs) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                childNames
        );
        spinnerChild.setAdapter(adapter);

        spinnerChild.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                presenter.onChildSelected(childIDs.get(pos));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }


    @Override
    public void displayThresholds(BadgeThresholds t) {
        etPerfectWeek.setText(String.valueOf(t.perfectWeekGoal));
        etTechnique.setText(String.valueOf(t.techniqueGoal));
        etLowRescue.setText(String.valueOf(t.lowRescueGoal));
    }

    @Override
    public void displayBadges(List<Badge> badges) {
        badgeContainer.removeAllViews();

        if (badges.isEmpty())
        {
            TextView tv = new TextView(this);
            tv.setText("No badges earned yet.");
            tv.setPadding(12, 12, 12, 12);
            badgeContainer.addView(tv);
            return;
        }

        for (Badge b : badges)
        {
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
    public String getPerfectWeekInput() {
        return etPerfectWeek.getText().toString().trim();
    }
    @Override
    public String getTechniqueInput() {
        return etTechnique.getText().toString().trim();
    }
    @Override
    public String getLowRescueInput() {
        return etLowRescue.getText().toString().trim();
    }

    @Override
    public void showError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void showSuccess(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigateHome() {
        startActivity(new Intent(this, MainActivityView.class));
        finish();
    }
}
