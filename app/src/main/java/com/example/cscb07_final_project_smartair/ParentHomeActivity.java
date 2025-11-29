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
    }

    private void loadRescueTrend(String childId, int days) {

        long now = System.currentTimeMillis();

        // Load last 30 days
        long startTime = now - ((long) 30 * 24 * 60 * 60 * 1000);

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
}