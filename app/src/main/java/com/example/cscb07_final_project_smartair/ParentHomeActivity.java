package com.example.cscb07_final_project_smartair;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cscb07_final_project_smartair.Views.BaseParentActivity;
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

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ParentHomeActivity extends BaseParentActivity {

    private LineChart trendChart;
    private TextView toggleText;
    private boolean showThirtyDays = false;
    private DatabaseReference mdatabase;
    String activeChildId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_home);

        // Get a database instance
        mdatabase = FirebaseDatabase.getInstance().getReference();

        trendChart = findViewById(R.id.chart_trend);
        toggleText = findViewById(R.id.tv_toggle_range);

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

        getTodayZone();
        getLastRescueTime();
        getWeeklyRescueCount();
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
                .child("rescueLogs");

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

    public void getTodayZone() {
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
                double ratio = -1024;
                for (DataSnapshot child : pefReceiver.getChildren()) {
                    Long timestamp = child.child("timestamp").getValue(Long.class);
                    Integer current = child.child("current").getValue(Integer.class);
                    Integer pb = child.child("pb").getValue(Integer.class);

                    // if valid daya
                    if (timestamp != null && current != null && pb != null &&
                            timestamp >= startOfToday && timestamp <= now) {

                        ratio = (double) current / pb;
                        break;
                    }
                }

                String zone = "No Data Today";

                if (ratio >= 0.8) {
                    zone = "Green";
                }
                else if (ratio >= 0.5) {
                    zone = "Yellow";
                }

                else zone = "Red";

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
                .child("rescueLogs");

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
                .child("rescueLogs");

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