package com.example.cscb07_final_project_smartair;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ParentAlertsActivity extends AppCompatActivity {

    private LineChart trendChart;
    private TextView toggleText;
    private boolean showThirtyDays = false;
    private String parentId;
    private DatabaseReference mdatabase;
    String activeChildId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_home);

        // Get a database instance
        mdatabase = FirebaseDatabase.getInstance().getReference();

        // Get parent ID from preferences
        // MODE_PRIVATE - Only accessible by calling app
        parentId = getSharedPreferences("APP_PREFS", MODE_PRIVATE)
                .getString("PARENT_ID", null);

        // ID found
        if (parentId != null) {
            listenForChildrenAlerts();
        }

        trendChart = findViewById(R.id.chart_trend);
        toggleText = findViewById(R.id.tv_toggle_range);

        // load7DayTrend();


        // If 'Show 30 Days' is pressed, this code will change the view to 30 days
        toggleText.setOnClickListener(v -> {
            if (showThirtyDays) {
                loadRescueTrend(activeChildId, 7);
                toggleText.setText("Show 30 days");
                showThirtyDays = false;
            } else {
                loadRescueTrend(activeChildId, 30);
                toggleText.setText("Show 7 days");
                showThirtyDays = true;
            }
        });
    }

    // Data to be updated to be live instead of hard coded

//    private void load7DayTrend() {
//        int[] data = {1, 0, 2, 1, 3, 1, 0, 2};
//        loadTrendDataIntoChart(data);
//    }
//
//    private void load30DayTrend() {
//        int[] data = new int[] {
//                1,0,1,1,2,1,0,1,1,3,
//                2,1,0,1,0,2,1,1,3,1,
//                0,2,2,1,1,0,1,2,1,0
//        };
//        loadTrendDataIntoChart(data);
//    }

    private void loadRescueTrend(String childId, int days) {

        long now = System.currentTimeMillis();

        // Load last 30 days
        long startTime = now - (30L * 24 * 60 * 60 * 1000);

        // Goes to the rescueLogs branch of the requested child based on the childId
        DatabaseReference rescueRef =
                mdatabase.child("children").child(childId).child("rescueLogs");

        ValueEventListener rescueListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot receiver) {

                // Array size based on 7 days or 30 days
                int[] counts = new int[days];

                for (DataSnapshot childSnap : receiver.getChildren()) {
                    Long timestamp = childSnap.child("timestamp").getValue(Long.class);
                    if (timestamp == null) continue;

                    if (timestamp >= startTime) {
                        int dayIndex = (int)((now - timestamp) / (24 * 60 * 60 * 1000));
                        if (dayIndex < days) {
                            counts[(days - 1) - dayIndex]++;
                        }
                    }
                }

                loadTrendDataIntoChart(counts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };

        rescueRef.addListenerForSingleValueEvent(rescueListener);
    }

    private void loadTrendDataIntoChart(int[] data) {

        ArrayList<Entry> entries = new ArrayList<>();

        for (int i = 0; i < data.length; i++) {
            entries.add(new Entry(i, data[i]));
        }

        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setColor(Color.parseColor("#4673DB"));
        dataSet.setLineWidth(2f);
        // dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#4673DB"));
        dataSet.setFillAlpha(60);

        LineData lineData = new LineData(dataSet);
        trendChart.setData(lineData);

        trendChart.getDescription().setEnabled(false);
        trendChart.getLegend().setEnabled(false);
        trendChart.setTouchEnabled(false);

        XAxis xAxis = trendChart.getXAxis();
        xAxis.setEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(12f);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setGranularity(1f); // units per label
        xAxis.setGranularityEnabled(true);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(data.length - 1);

        // To check: do all cases always work

        if (data.length == 30) {
            xAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    int idx = Math.round(value);
                    switch (idx) {
                        case 0: return "0";
                        case 5: return "5";
                        case 10: return "10";
                        case 15: return "15";
                        case 19: return "20";
                        case 24: return "25";
                        case 29: return "30";
                        default: return "";
                    }
                }
            });
        }

        else {

            xAxis.setLabelCount(data.length + 1, true);

            xAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.valueOf(Math.round(value));
                }
            });
        }

        YAxis leftAxis = trendChart.getAxisLeft();

        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        leftAxis.setEnabled(true);
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setTextSize(12f);

        YAxis rightAxis = trendChart.getAxisRight();
        rightAxis.setEnabled(false);

        trendChart.invalidate();
    }

    // Listens to alerts for each child
    private void listenForChildrenAlerts() {

        ValueEventListener childListener = new ValueEventListener() {

            // Iterates through all children and runs whenever child/data is added,
            // removed or updated
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnap : dataSnapshot.getChildren()) {
                    String childId = childSnap.getKey();
                    if (childSnap.hasChild(parentId)) {

                        if (activeChildId == null) {
                            activeChildId = childId;

                            // Load default 7-day trend
                            loadRescueTrend(activeChildId, 7);
                        }

                        // Listeners for each alert category
                        listenTriage(childId);
                        listenTodayZone(childId);
                        listenRapidRescue(childId);
                        listenInventory(childId);
                    }
                }
            }

            // Handles read errors
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ParentListener", "Failed to read children",
                        databaseError.toException());
            }
        };

        // Listens to the data in the 'parentsByChild' node
        DatabaseReference childrenRef = mdatabase.child("parentsByChild");
        childrenRef.addValueEventListener(childListener);
    }

    // Today zone listener, alert for the child's zone today
    // Listens to the child based on the ID passed as an argument
    private void listenTodayZone(String childId) {
        DatabaseReference zoneRef =
                mdatabase.child("children").child(childId).child("todayZone");

        ValueEventListener zoneListener = new ValueEventListener() {

            // Runs whenever data under today's zone changes
            @Override
            public void onDataChange(@NonNull DataSnapshot receiver) {
                String zone = receiver.getValue(String.class);

                // Notifies the parent if the zone is red
                if ("red".equals(zone)) {
                    showAlert("Red Zone Alert",
                            "Your child is in the RED asthma zone today.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };

        zoneRef.addValueEventListener(zoneListener);
    }

    // Rapid rescue repeats listener, listens for rescue uses
    private void listenRapidRescue(String childId) {
        DatabaseReference logsRef =
                mdatabase.child("children").child(childId).child("rescueLogs");

        ChildEventListener logsListener = new ChildEventListener() {

            // Runs when a new rescue log is added
            @Override
            public void onChildAdded(@NonNull DataSnapshot childSnap, String previousChildName) {

                ValueEventListener rescueListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot parentSnap) {
                        long now = System.currentTimeMillis();
                        int count = 0;
                        for (DataSnapshot childSnap : parentSnap.getChildren()) {
                            Long timestamp = childSnap.child("timestamp").getValue(Long.class);

                            // Between now and 3 hours ago
                            // Convert milliseconds to hours
                            if (timestamp != null && timestamp >= now - 3*60*60*1000) {
                                count++;
                            }
                        }

                        // Alerts if 3 or more rescue uses in the last 3 hours
                        if (count >= 3) {
                            showAlert("Rescue Inhaler Alert",
                                    "Your child has used rescue inhaler 3 times in 3 hours.");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                };

                // Reads all rescue logs of a child
                Objects.requireNonNull(childSnap.
                        getRef().getParent()).addListenerForSingleValueEvent(rescueListener);
            }

            // Following functions are not needed
            // Detects if a rescue time has changed
            @Override
            public void onChildChanged(@NonNull DataSnapshot childSnap,
                                       String previousChildName) {}

            // Detects if a rescue time has been removed
            @Override
            public void onChildRemoved(@NonNull DataSnapshot childSnap) {}

            // Detects if the order of rescue logs has changed
            @Override
            public void onChildMoved(@NonNull DataSnapshot childSnap,
                                     String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };

        logsRef.addChildEventListener(logsListener);
    }

    // Worse after dose listener, checks and alerts if the child is feeling worse after a dose
    private void listenWorseAfterDose(String childId) {
        DatabaseReference doseRef =
                mdatabase.child("children").child(childId).child("doseChecks");

        ChildEventListener childListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot receiver, String previousChildName) {

                // Gets the child's condition after the dose and stores it in 'after'
                String after = receiver.child("after").getValue(String.class);

                // If worse, alert the parent
                if ("Worse".equals(after)) {
                    showAlert("Dose Check: Worse",
                            "Your child reported feeling worse after their dose.");
                }
            }

            // Following functions are not needed
            @Override public void onChildChanged(@NonNull DataSnapshot childSnap, String previousChildName) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot childSnap) {}
            @Override public void onChildMoved(@NonNull DataSnapshot childSnap, String previousChildName) {}
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        };

        doseRef.addChildEventListener(childListener);
    }

    // Triage escalation listener
    private void listenTriage(String childId) {
        DatabaseReference triageRef =
                mdatabase.child("children").child(childId).child("triage");

        ChildEventListener childListener = new ChildEventListener() {

            // After a new triage session, checks if escalated
            @Override
            public void onChildAdded(@NonNull DataSnapshot receiver, String previousChildName) {

                // Finds whether escalated is true or false
                Boolean escalated = receiver.child("escalated").getValue(Boolean.class);
                if (Boolean.TRUE.equals(escalated)) {
                    showAlert("Triage Escalation",
                            "Your child's triage has escalated.");
                }
            }

            // Following functions are not needed
            @Override public void onChildChanged(@NonNull DataSnapshot receiver, String previousChildName) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot receiver) {}
            @Override public void onChildMoved(@NonNull DataSnapshot receiver, String previousChildName) {}
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        };

        triageRef.addChildEventListener(childListener);
    }

    // Inventory low/expired listener, alerts the parent if the child's medication is low
    // or expired
    private void listenInventory(String childId) {
        DatabaseReference inventoryRef =
                mdatabase.child("children").child(childId).child("inventory");

        ChildEventListener childListener = new ChildEventListener() {

            // Runs if a new medication is added
            @Override
            public void onChildAdded(@NonNull DataSnapshot receiver, String previousChildName) {
                checkInventory(receiver);
            }

            // Runs anytime the medication level changes or the medication becomes expired
            @Override
            public void onChildChanged(@NonNull DataSnapshot receiver, String previousChildName) {
                checkInventory(receiver);
            }

            // Following function are not needed
            @Override
            public void onChildRemoved(@NonNull DataSnapshot receiver) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot receiver, String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}

            // Checks whether the medicine's low or expired indicators are true and alerts
            // the parent accordingly
            private void checkInventory(DataSnapshot receiver) {

                Boolean expired = receiver.child("expired").getValue(Boolean.class);
                Boolean low = receiver.child("low").getValue(Boolean.class);
                if (Boolean.TRUE.equals(expired)) {
                    showAlert("Expired Medication",
                            "Your child's medication has expired.");
                }
                if (Boolean.TRUE.equals(low)) {
                    showAlert("Low Inhaler", "Your child's inhaler is low.");
                }
            }
        };

        inventoryRef.addChildEventListener(childListener);
    }

    // Displays a popup method for all the alerts on the parent's app
    private void showAlert(String title, String message) {
        // Runs on the main UI thread, instead of a background thread
        // and shows the alert
        runOnUiThread(() -> Toast.makeText(this,
                title + ": " + message, Toast.LENGTH_LONG).show());
    }
}