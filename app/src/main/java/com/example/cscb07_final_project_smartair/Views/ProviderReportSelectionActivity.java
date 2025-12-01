package com.example.cscb07_final_project_smartair.Views;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.cscb07_final_project_smartair.Helpers.ProviderReportGenerator;
import com.example.cscb07_final_project_smartair.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ProviderReportSelectionActivity extends BaseParentActivity {

    private Spinner spinnerChild;
    private HashMap<String, String> nameToIdMap = new HashMap<>();
    private String selectedChildId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_report_selection);

        spinnerChild = findViewById(R.id.SDspinnerChild);
        Button btn3Months = findViewById(R.id.btn3Months);
        Button btn6Months = findViewById(R.id.btn6Months);

        String parentId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        loadChildren(parentId);

        btn3Months.setOnClickListener(view -> {
            if (selectedChildId != null) {
                new ProviderReportGenerator().generateReport(this, selectedChildId, 3);
            }
        });

        btn6Months.setOnClickListener(view -> {
            if (selectedChildId != null) {
                new ProviderReportGenerator().generateReport(this, selectedChildId, 6);
            }
        });
    }

    private void loadChildren(String parentId) {
        DatabaseReference childrenRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child("parents")
                .child(parentId)
                .child("children");

        childrenRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot receiver) {
                ArrayList<String> names = new ArrayList<>();
                for (DataSnapshot child : receiver.getChildren()) {
                    String childId = child.getKey(); // get child id
                    String fullName = child.child("name").getValue(String.class);
                    if (childId != null && fullName != null) {
                        names.add(fullName);
                        nameToIdMap.put(fullName, childId);
                    }
                }

                if (!names.isEmpty()) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(ProviderReportSelectionActivity.this,
                            android.R.layout.simple_spinner_item, names);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerChild.setAdapter(adapter);

                    selectedChildId = nameToIdMap.get(names.get(0));
                    spinnerChild.setSelection(0);
                }

                spinnerChild.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {

                    // runs whenever a child is selected and gets the name at that position
                    // and gets the child's id
                    @Override
                    public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                        String name = (String) parent.getItemAtPosition(position);
                        selectedChildId = nameToIdMap.get(name);
                    }

                    @Override
                    public void onNothingSelected(android.widget.AdapterView<?> parent) {
                        selectedChildId = null;
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProviderReportSelectionActivity.this, "Failed to load children", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
