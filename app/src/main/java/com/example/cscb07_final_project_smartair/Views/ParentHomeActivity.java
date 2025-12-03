package com.example.cscb07_final_project_smartair.Views;

import android.graphics.Color;
import android.os.Bundle;

import android.content.Intent;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.cscb07_final_project_smartair.Presenters.ParentHomePresenter;
import com.example.cscb07_final_project_smartair.R;
import com.example.cscb07_final_project_smartair.Users.ChildSpinnerOption;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ParentHomeActivity extends BaseParentActivity implements ParentHomeView{
    private ParentHomePresenter presenter;
    private LineChart trendChart;
    private TextView toggleText;
    private Spinner spinnerChild;

    private boolean showThirtyDays = false;
    public String activeChildId = null;

    private DatabaseReference mdatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_home);

        if (mdatabase == null) {
            mdatabase = FirebaseDatabase.getInstance().getReference();
        }

        trendChart = findViewById(R.id.chart_trend);
        toggleText = findViewById(R.id.tv_toggle_range);
        spinnerChild = findViewById(R.id.SDspinnerChild);

        // If 'Show 30 Days' is pressed, this code will change the view to 30 days
        toggleText.setOnClickListener(v -> {
            if(activeChildId == null) {return;}
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

        presenter = new ParentHomePresenter(this);
        presenter.loadChildrenDash();

        Button check_in_button = findViewById(R.id.checkin);
        Button btnLogs = findViewById(R.id.btnMedicineLogs);
        Button btnPEF = findViewById(R.id.btnPEF);
        Button btnCheckInHistory = findViewById(R.id.btnCheckInHistory);

        Button logout_button = findViewById(R.id.logout);
        Button btnInventory = findViewById(R.id.btnInventory);
        Button btnProviderReport = findViewById(R.id.btnProviderReport);
        Button btnSchedule = findViewById(R.id.btnSchedule);
        Button btnBadgeSettings = findViewById(R.id.btnBadgeSettings);
        Button btnManageChildren = findViewById(R.id.btnManageChildren);
        Button btnInvites = findViewById(R.id.btnProviderInvites);

        check_in_button.setOnClickListener(v -> presenter.onCheckInButtonClicked());
        btnCheckInHistory.setOnClickListener(v -> presenter.onCheckInHistoryClicked());
        btnLogs.setOnClickListener(v -> presenter.onMedicineLogsClicked());
        btnPEF.setOnClickListener(v -> presenter.onPEFButtonClicked());

        btnBadgeSettings.setOnClickListener(v -> presenter.onBadgeSettingsClicked());
        btnSchedule.setOnClickListener(v -> presenter.onScheduleButtonClicked());
        btnInventory.setOnClickListener(v -> presenter.onInventoryClicked());
        btnProviderReport.setOnClickListener(v -> presenter.onProviderReportClicked());
        btnManageChildren.setOnClickListener(v -> presenter.onManageChildrenClicked());
        btnInvites.setOnClickListener(v -> presenter.onInvitesClicked());

        logout_button.setOnClickListener(v -> presenter.onLogoutButtonClicked());
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (presenter != null) {
            presenter.loadChildrenDash();
        }
    }

    public void setActiveChild(String id) {
        activeChildId = id;

        loadRescueTrend(id, showThirtyDays ? 30 : 7);
        getTodayZone();
        getLastRescueTime();
        getWeeklyRescueCount();
    }

    public void displayChildren(List<ChildSpinnerOption> names) {
        ArrayAdapter<ChildSpinnerOption> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerChild.setAdapter(adapter);

        if (!names.isEmpty()) {
            spinnerChild.setSelection(0);
            presenter.onChildSelectedDash(0); // auto select a child
        }

        spinnerChild.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                presenter.onChildSelectedDash(pos);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    public void navigateToRoleSelectionScreen(){
        Intent intent = new Intent(this, RoleLauncherActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void navigateToCheckInScreen(){
        startActivity(new Intent(this, CheckInActivity.class));
    }

    @Override
    public void navigateToCheckInHistoryScreen(){
        startActivity(new Intent(this, CheckInHistoryActivity.class));
    }

    @Override
    public void navigateToMedicineLogs() {
        startActivity(new Intent(this, MedicineLogsActivity.class));
    }
    @Override
    public void navigateToBadgeSettings() {
        startActivity(new Intent(this, BadgeSettingsActivity.class));
    }

    @Override
    public void navigateToManageChildren(){
        startActivity(new Intent(this, ManageChildrenActivity.class));
    }
    @Override
    public void navigateToPEFEntry() {
        startActivity(new Intent(this, PEFActivity.class));
    }
    @Override
    public void navigateToSchedule() {
        startActivity(new Intent(this, ScheduleActivity.class));
    }

    @Override
    public void navigateToInventory() {
        startActivity(new Intent(this, InventoryActivity.class));
    }

    @Override
    public void navigateToProviderReport() {
        startActivity(new Intent(this, ProviderReportSelectionActivity.class));
    }
    @Override
    public void navigateToInvites() {
        startActivity(new Intent(this, InvitesActivity.class));
    }

    private void loadRescueTrend(String childId, int days) {

        long now = System.currentTimeMillis();

        // Load last 30 days
        long startTime = now - ((long) days * 24 * 60 * 60 * 1000);

        // Goes to the rescueLogs branch of the requested child based on the childId
        DatabaseReference rescueRef = mdatabase
                .child("users")
                .child("children")
                .child(childId)
                .child("medicine")
                .child("rescue");

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

        // increment by units of 5 on x axis for 30 day chart
        if (data.length == 30) {
            xAxis.setLabelCount(data.length, false);

            String[] labels = new String[data.length];

            for (int i = 0; i < data.length; i++) {
                // label multiples of 5
                if (i == 0 || i == 5 || i == 10 || i == 15 || i == 20 || i == 25) {
                    labels[i] = String.valueOf(i);
                }

                else if (i == 29) {
                    labels[i] = String.valueOf(i + 1);
                }

                else {
                    labels[i] = "";
                }
            }

            xAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    int index = Math.round(value);

                    if (index >= 0 && index < labels.length) {
                        return labels[index];
                    }

                    return "";
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

        int maxValue = 0;
        for (int item : data) {
            if (item > maxValue) maxValue = item;
        }

        leftAxis.setAxisMaximum(maxValue + 1); // y axis scale
        leftAxis.setLabelCount(Math.min(maxValue + 1, 6), true); // ensures there is not
        // too many ticks on the
        // y axis

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

    public void getTodayZone() {
        if (activeChildId == null) return;

        DatabaseReference pefRef = mdatabase
                .child("pef")
                .child(activeChildId);

        long now = System.currentTimeMillis();

        TextView tvTodayZoneValue = findViewById(R.id.tv_today_zone_value);

        // Find the time the day started in milliseconds to find if there is
        // any pef log today
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        long startOfToday = cal.getTimeInMillis();

        pefRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot pefReceiver) {
                double lowestRatio = 10;
                boolean foundRatio = false;
                for (DataSnapshot child : pefReceiver.getChildren()) {
                    Long timestamp = child.child("timestamp").getValue(Long.class);
                    Integer current = child.child("current").getValue(Integer.class);
                    Integer pb = child.child("pb_pef").getValue(Integer.class);

                    // if valid daya
                    if (timestamp != null && current != null && pb != null &&
                            timestamp >= startOfToday && timestamp <= now) {

                        double ratio = ((double) current) / pb;
                        foundRatio = true;

                        if (ratio < lowestRatio) {
                            lowestRatio = ratio; // lowest ratio of the day
                        }
                    }
                }

                String zone = "No Data";

                if (foundRatio)
                {
                    if (lowestRatio >= 0.8) {
                        zone = "Green";
                    }
                    else if (lowestRatio >= 0.5) {
                        zone = "Yellow";
                    }

                    else if (lowestRatio > 0) {
                        zone = "Red";
                    }
                }

                tvTodayZoneValue.setText(zone);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public void getLastRescueTime() {
        DatabaseReference rescueRef = mdatabase
                .child("users")
                .child("children")
                .child(activeChildId)
                .child("medicine")
                .child("rescue");

        rescueRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot receiver) {
                long latestTimestamp = 0;

                for (DataSnapshot child : receiver.getChildren()) {
                    Long timestamp = child.child("timestamp").getValue(Long.class);
                    if (timestamp != null && timestamp > latestTimestamp) {
                        latestTimestamp = timestamp;
                    }
                }

                if (latestTimestamp > 0) {
                    java.text.SimpleDateFormat date = new java.text.SimpleDateFormat("MM/dd/yyyy hh:mm a");
                    String formattedTime = date.format(new java.util.Date(latestTimestamp));
                    TextView tvLastRescue = findViewById(R.id.tv_tile_rescue_value);
                    tvLastRescue.setText(formattedTime);
                }

                else {
                    TextView tvLastRescue = findViewById(R.id.tv_tile_rescue_value);
                    tvLastRescue.setText("No Data");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public void getWeeklyRescueCount() {
        long now = System.currentTimeMillis();
        long lastWeek = now - ((long)7 * 24 * 60 * 60 * 1000);

        DatabaseReference rescueRef = mdatabase
                .child("users")
                .child("children")
                .child(activeChildId)
                .child("medicine")
                .child("rescue");

        rescueRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot receiver) {
                int count = 0;
                for (DataSnapshot child : receiver.getChildren()) {
                    Long timestamp = child.child("timestamp").getValue(Long.class);
                    if (timestamp != null && timestamp >= lastWeek) {
                        count++;
                    }
                }

                TextView tvWeeklyCount = findViewById(R.id.tv_tile_weekly_value);
                tvWeeklyCount.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
