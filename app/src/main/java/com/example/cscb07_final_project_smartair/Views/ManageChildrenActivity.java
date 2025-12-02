package com.example.cscb07_final_project_smartair.Views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cscb07_final_project_smartair.R;
import com.example.cscb07_final_project_smartair.Presenters.ManageChildrenPresenter;
import com.example.cscb07_final_project_smartair.Users.ChildPermissions;

import java.util.List;

public class ManageChildrenActivity extends AppCompatActivity implements ManageChildrenView {
    private Spinner spinner, providers, invites;
    private CheckBox rescue, adherence, symptoms, triggers, pef, triage, summary;
    private Button add, generate, revoke;
    private ManageChildrenPresenter presenter;

    private final CompoundButton.OnCheckedChangeListener rescueListener = (view, isChecked) -> presenter.onRescueChanged(isChecked),
    adherenceListener = (view, isChecked) -> presenter.onAdherenceChanged(isChecked),
    symptomsListener = (view, isChecked) -> presenter.onSymptomsChanged(isChecked),
    triggersListener = (view, isChecked) -> presenter.onTriggersChanged(isChecked),
    pefListener = (view, isChecked) -> presenter.onPefChanged(isChecked),
    triageListener = (view, isChecked) -> presenter.onTriageChanged(isChecked),
    summaryListener = (view, isChecked) -> presenter.onSummaryChanged(isChecked);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_children);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spinner = findViewById(R.id.spinnerChildren);
        providers = findViewById(R.id.providers);
        rescue = findViewById(R.id.rescueLogs);
        adherence = findViewById(R.id.adherence);
        symptoms = findViewById(R.id.symptomsCheck);
        triggers = findViewById(R.id.triggersCheck);
        pef = findViewById(R.id.pef);
        triage = findViewById(R.id.triage);
        summary = findViewById(R.id.summary);
        add = findViewById(R.id.add);

        invites = findViewById(R.id.spinnerInvites);
        generate = findViewById(R.id.addInvite);
        revoke = findViewById(R.id.revoke);

        presenter = new ManageChildrenPresenter(this);

        add.setOnClickListener(view -> presenter.onAddChildClicked());
        rescue.setOnCheckedChangeListener(rescueListener);
        adherence.setOnCheckedChangeListener(adherenceListener);
        symptoms.setOnCheckedChangeListener(symptomsListener);
        triggers.setOnCheckedChangeListener(triggersListener);
        pef.setOnCheckedChangeListener(pefListener);
        triage.setOnCheckedChangeListener(triageListener);
        summary.setOnCheckedChangeListener(summaryListener);

        presenter.loadChildren();
    }

    @Override
    public void displayChildren(List<String> names) {
        displayNames(names, spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
                presenter.onChildSelected(i);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    public void displayProviders(List<String> names) {
        displayNames(names, providers);
        providers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
                presenter.onProviderSelected(i);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    public void displayPerms(ChildPermissions perms) {
        if (perms.rescueLogs != rescue.isChecked()) {
            rescue.setOnCheckedChangeListener(null);
            rescue.setChecked(perms.rescueLogs);
            rescue.setOnCheckedChangeListener(rescueListener);
        }
        if (perms.contrSummary != adherence.isChecked()) {
            adherence.setOnCheckedChangeListener(null);
            adherence.setChecked(perms.contrSummary);
            adherence.setOnCheckedChangeListener(adherenceListener);
        }
        if (perms.symptoms != symptoms.isChecked()) {
            symptoms.setOnCheckedChangeListener(null);
            symptoms.setChecked(perms.symptoms);
            symptoms.setOnCheckedChangeListener(symptomsListener);
        }
        if (perms.triggers != rescue.isChecked()) {
            triggers.setOnCheckedChangeListener(null);
            triggers.setChecked(perms.triggers);
            triggers.setOnCheckedChangeListener(triggersListener);
        }
        if (perms.pef != pef.isChecked()) {
            pef.setOnCheckedChangeListener(null);
            pef.setChecked(perms.pef);
            pef.setOnCheckedChangeListener(pefListener);
        }
        if (perms.triageIncidents != triage.isChecked()) {
            triage.setOnCheckedChangeListener(null);
            triage.setChecked(perms.triageIncidents);
            triage.setOnCheckedChangeListener(triageListener);
        }
        if (perms.summaryCharts != summary.isChecked()) {
            summary.setOnCheckedChangeListener(null);
            summary.setChecked(perms.summaryCharts);
            summary.setOnCheckedChangeListener(summaryListener);
        }
    }

    private void displayNames(List<String> names, Spinner s) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        s.setAdapter(adapter);
    }

    @Override
    public void navigateToAddChild() {
        Intent intent = new Intent(this, AddChildActivity.class);
        startActivity(intent);
        finish();
    }

    public void showSuccess(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    public void showError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}