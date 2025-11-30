package com.example.cscb07_final_project_smartair.Views;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cscb07_final_project_smartair.Adapters.CheckInHistoryAdapter;
import com.example.cscb07_final_project_smartair.DataObjects.CheckInData;
import com.example.cscb07_final_project_smartair.Presenters.CheckInHistoryPresenter;
import com.example.cscb07_final_project_smartair.R;

import java.util.ArrayList;

public class CheckInHistoryActivity extends BaseActivity implements CheckInHistoryView {

    private CheckInHistoryAdapter adapter;
    private RecyclerView recyclerView;
    private EditText symptomFilter;
    private EditText triggerFilter;
    private EditText dateRange;
    private Button filter_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin_history);

        CheckInHistoryPresenter presenter = new CheckInHistoryPresenter(this);
        recyclerView = findViewById(R.id.check_in_items);
        symptomFilter = findViewById(R.id.symptom_filter);
        triggerFilter = findViewById(R.id.trigger_filter);
        dateRange = findViewById(R.id.date_range);
        filter_button = findViewById(R.id.filter_button);

        filter_button.setOnClickListener(v -> {
            presenter.onFilterButtonClicked();
        });

        setupRecyclerView();

        presenter.showCheckInHistory();

    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CheckInHistoryAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void displayCheckInHistory(ArrayList<CheckInData> checkIns) {
        recyclerView.setVisibility(View.VISIBLE);
        adapter.updateData(checkIns);
    }

    public void showSearchSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    public void showSearchFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public ArrayList<String> getSymptoms() {
        ArrayList<String> symptoms = new ArrayList<>();
        String[] split_symptoms = symptomFilter.getText().toString().split(",");
        for (String symptom : split_symptoms) {
            if (!symptom.isEmpty()) {
                symptoms.add(symptom.trim().toLowerCase());
            }
        }
        return symptoms;
    }

    @Override
    public ArrayList<String> getTriggers() {
        ArrayList<String> triggers = new ArrayList<>();
        String[] split_triggers = triggerFilter.getText().toString().split(",");
        for (String trigger: split_triggers) {
            if (!trigger.isEmpty()) {
                triggers.add(trigger.trim().toLowerCase());
            }
        }
        return triggers;
    }

    @Override
    public String[] getDateRange() {
        String date = dateRange.getText().toString();
        String[] split_date = date.split("-");
        return split_date;
    }
}


