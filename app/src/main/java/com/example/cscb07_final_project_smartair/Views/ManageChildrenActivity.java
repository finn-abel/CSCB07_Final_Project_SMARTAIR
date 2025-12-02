package com.example.cscb07_final_project_smartair.Views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cscb07_final_project_smartair.Adapters.ManageChildAdapter;
import com.example.cscb07_final_project_smartair.R;
import com.example.cscb07_final_project_smartair.Presenters.ManageChildrenPresenter;
import com.example.cscb07_final_project_smartair.Users.ChildSpinnerOption;
import com.example.cscb07_final_project_smartair.Users.ChildPermissions;

import java.util.List;

public class ManageChildrenActivity extends BaseActivity implements ManageChildrenView {
    private RecyclerView childrenDisplay;
    private Spinner providers;
    private CheckBox rescue, adherence, symptoms, triggers, pef, triage, summary;
    private Button add;
    private ManageChildrenPresenter presenter;
    private View permissionLayout;

    private TextView noProvidersMsg;

    private TextView select_providers_label;

    private EditText redGuidance;
    private EditText yellowGuidance;
    private EditText greenGuidance;
    private EditText pefPB;
    private Button goToPermissions;
    private Button goToPEF;
    private Button savePEF;
    private TextView permissionsHeader;
    private TextView pefHeader;
    private ScrollView scroll;


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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        //initialize items

        childrenDisplay = findViewById(R.id.childrenDisplay);
        childrenDisplay.setLayoutManager(new LinearLayoutManager(this));
        permissionLayout = findViewById(R.id.permissionLayout);
        noProvidersMsg = findViewById(R.id.no_providers_msg);
        select_providers_label = findViewById(R.id.select_provider_label);
        scroll = findViewById(R.id.manage_children_scroll);

        redGuidance = findViewById(R.id.red_guidance_entry);
        yellowGuidance = findViewById(R.id.yellow_guidance_entry);
        greenGuidance = findViewById(R.id.green_guidance_entry);
        pefPB = findViewById(R.id.editPEFpb);
        goToPEF = findViewById(R.id.edit_pef_details_btn);
        goToPermissions = findViewById(R.id.provider_permissions_btn);
        savePEF = findViewById(R.id.save_pef_guidance);
        permissionsHeader = findViewById(R.id.permissions_header);
        pefHeader = findViewById(R.id.pef_header);

        providers = findViewById(R.id.providers);
        rescue = findViewById(R.id.rescueLogs);
        adherence = findViewById(R.id.adherence);
        symptoms = findViewById(R.id.symptomsCheck);
        triggers = findViewById(R.id.triggersCheck);
        pef = findViewById(R.id.pef);
        triage = findViewById(R.id.triage);
        summary = findViewById(R.id.summary);
        add = findViewById(R.id.add_child_btn);

        //initialize UI view
        permissionLayout.setVisibility(View.GONE);
        noProvidersMsg.setVisibility(View.VISIBLE);
        providers.setVisibility(View.GONE);
        select_providers_label.setVisibility(View.GONE);

        presenter = new ManageChildrenPresenter(this);

        add.setOnClickListener(view -> presenter.onAddChildClicked());
        rescue.setOnCheckedChangeListener(rescueListener);
        adherence.setOnCheckedChangeListener(adherenceListener);
        symptoms.setOnCheckedChangeListener(symptomsListener);
        triggers.setOnCheckedChangeListener(triggersListener);
        pef.setOnCheckedChangeListener(pefListener);
        triage.setOnCheckedChangeListener(triageListener);
        summary.setOnCheckedChangeListener(summaryListener);

        savePEF.setOnClickListener( v -> {
            String red = redGuidance.getText().toString().trim();
            String yellow = yellowGuidance.getText().toString().trim();
            String green = greenGuidance.getText().toString().trim();
            String pb = pefPB.getText().toString().trim();
            presenter.onSavePefClicked(red,yellow,green,pb);
        });

        //scroll to relevant section on page

        goToPermissions.setOnClickListener(v -> {
            int targetY = permissionsHeader.getTop();
            scroll.smoothScrollTo(0, targetY);
        });
        goToPEF.setOnClickListener(v -> {
            int targetY = pefHeader.getTop();
            scroll.smoothScrollTo(0, targetY);
        });

        presenter.loadChildren();
    }

    @Override
    public void displayChildren(List<ChildSpinnerOption> childrenList) {
        ManageChildAdapter adapter = new ManageChildAdapter(childrenList, new ManageChildAdapter.OnChildClickListener() {
            @Override
            public void onChildClick(int position, String childId) {
                presenter.onChildSelected(childId);
            }
        });
        childrenDisplay.setAdapter(adapter);
    }

    @Override
    public void displayProviders(List<ChildPermissions> providerList) {
        ArrayAdapter<ChildPermissions> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                providerList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        providers.setAdapter(adapter);

        providers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
                presenter.onProviderSelected(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
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


    @Override
    public void updateUI(int state){
        if(state == 0){ //not loaded yet
            permissionLayout.setVisibility(View.GONE);
            noProvidersMsg.setVisibility(View.GONE);
            providers.setVisibility(View.GONE);
            select_providers_label.setVisibility(View.GONE);
        } else if (state == 1){ //no providers
            permissionLayout.setVisibility(View.GONE);
            noProvidersMsg.setVisibility(View.VISIBLE);
            providers.setVisibility(View.GONE);
            select_providers_label.setVisibility(View.GONE);
        } else { //providers found
            permissionLayout.setVisibility(View.VISIBLE);
            noProvidersMsg.setVisibility(View.GONE);
            providers.setVisibility(View.VISIBLE);
            select_providers_label.setVisibility(View.GONE);
        }
    }


    @Override
    public void populatePefFields(String red, String yellow, String green, float pb) {
        // fill in text fields
        redGuidance.setText(red);
        yellowGuidance.setText(yellow);
        greenGuidance.setText(green);

        if (pb > 0) {
            pefPB.setText(String.valueOf(pb));
        } else {
            pefPB.setText("");
        }
    }

}