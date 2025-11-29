package com.example.cscb07_final_project_smartair.Views;

import android.os.Bundle;
import android.widget.Button;

import com.example.cscb07_final_project_smartair.Helpers.ProviderReportGenerator;
import com.example.cscb07_final_project_smartair.R;
import com.google.firebase.database.FirebaseDatabase;

public class ProviderReportSelectionActivity extends BaseParentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_report_selection);

        Button btn3Months = findViewById(R.id.btn3Months);
        Button btn6Months = findViewById(R.id.btn6Months);

        btn3Months.setOnClickListener(view -> {
            new ProviderReportGenerator().generateReport(this, "child1", 3);
        });

        btn6Months.setOnClickListener(view -> {
            new ProviderReportGenerator().generateReport(this, "child1", 6);
        });
    }
}
