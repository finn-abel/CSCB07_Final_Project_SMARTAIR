package com.example.cscb07_final_project_smartair.Helpers;

import static android.graphics.Bitmap.createBitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ProviderReportGenerator {

    private DatabaseReference mdatabase;

    public ProviderReportGenerator() {
        mdatabase = FirebaseDatabase.getInstance().getReference();
    }

    // Checks if the report is generatable
    // I.e. checks if there is enough data for the last 'months' months

    // Note: still have to check for every item, not just rescue frequency
    public void generateReport(Context context, String childId, int months) {

        if (childId == null)
        {
            // Report if no child id is null
            Toast.makeText(context, "No child ID", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference rescueRef = FirebaseDatabase.getInstance()
                .getReference("users")  // users folder
                .child("children")  // children folder
                .child(childId)  // child id's folder
                .child("rescueLogs"); // rescue logs of the child

        DatabaseReference pefRef = FirebaseDatabase.getInstance()
                .getReference("pef")  // pef folder
                .child(childId);  // childID's PEF logs

        DatabaseReference nameRef = FirebaseDatabase.getInstance()
                .getReference("users")  // user folder
                .child("children")  // children folder
                .child(childId)  // node matching child's id
                .child("name"); // name of the child

        DatabaseReference triageRef = FirebaseDatabase.getInstance()
                .getReference("users") // users folder
                .child("children") // children folder
                .child(childId) // child id's folder
                .child("permissions"); // permissions folder

        long now = System.currentTimeMillis();

        // Loads last 'months', 3 or 6, months
        // Converts milliseconds to months
        long startTime = now - ((long) months * 30 * 24 * 60 * 60 * 1000);

        rescueRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot receiver) {
                boolean dataBeforeExists = false;
                boolean dataAfterExists = false;
                int[] dailyFrequency = new int[months * 31]; // Accounts for longer months

                for (DataSnapshot childSnap : receiver.getChildren()) {
                    Long timestamp = childSnap.child("timestamp").getValue(Long.class);

                    if (timestamp == null)
                    {
                        continue; // Skip iteration if the timestamp is null
                    }

                    if (timestamp <= startTime)
                    {
                        dataBeforeExists = true; // There is at least 'months' (3 or 6) months of data
                    }

                    if (timestamp >= startTime) {
                        // Find the day of the timestamp
                        int day = (int) ((now - timestamp) / ((long)24 * 60 * 60 * 1000));

                        // If valid day index
                        if (day >= 0 && day < dailyFrequency.length)
                        {
                            dailyFrequency[day]++;
                        }

                        dataAfterExists = true; // There is data within the last 'months' (3 or 6) months
                    }
                }

                if (!dataBeforeExists || !dataAfterExists) {
                    Toast.makeText(context, "Not enough data 1", Toast.LENGTH_SHORT).show();
                    return;
                }

                pefRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot zoneReceiver) {
                        ArrayList<Entry> zoneEntries = new ArrayList<>();
                        boolean dataBeforeExists2 = false;
                        boolean dataAfterExists2 = false;

                        for (DataSnapshot childZoneSnap : zoneReceiver.getChildren()) {
                            Long zoneTimestamp =
                                    childZoneSnap.child("timestamp").getValue(Long.class);
                            Integer current =
                                    childZoneSnap.child("current").getValue(Integer.class);
                            Integer pb = childZoneSnap.child("pb").getValue(Integer.class);
                            // Integer to allow for null

                            if (zoneTimestamp == null || current == null)
                            {
                                continue; // skip if no values in database
                            }

                            if (zoneTimestamp <= startTime)
                            {
                                dataBeforeExists2 = true; // There is at least 'months' (3 or 6) months of data
                            }

                            if (zoneTimestamp >= startTime)
                            {
                                // Find the day of the timestamp
                                int day =
                                        (int) ((now - zoneTimestamp) / ((long)24 * 60 * 60 * 1000));

                                // If valid day index
                                if (day >= 0 && day < dailyFrequency.length)
                                {
                                    if (pb != null && pb != 0)
                                    {
                                        zoneEntries
                                                .add(new Entry(day, (((float) current) / pb) * 100));
                                    }

                                    else {
                                        Toast.makeText(context,
                                                "Invalid or missing personal best",
                                                Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }

                                dataAfterExists2 = true; // There is data within the last 'months' (3 or 6) months
                            }
                        }

                        if (!dataBeforeExists2 || !dataAfterExists2) {
                            Toast.makeText(context, "Not enough data 2", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        nameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot nameReceiver) {
                                String name = nameReceiver.getValue(String.class);

                                // Checks if permissions are enabled for triage sharing
                                triageRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot triageReceiver) {
                                        Boolean triageIncidentsEnabled = triageReceiver
                                                        .child("triageIncidents")
                                                        .getValue(Boolean.class);

                                        ArrayList<String> triageList = new ArrayList<>();

                                        if (triageIncidentsEnabled != null && triageIncidentsEnabled) {
                                            getTriageIncidents(childId, new TriageIncidentCallback() {
                                                        @Override
                                                        public void onTriageIncidentsLoaded(ArrayList<String> triageList) {
                                                            generatePdf(context, childId, months, dailyFrequency, zoneEntries, name, triageList);
                                                        }
                                                    });
                                        }

                                        else {
                                            generatePdf(context, childId, months,
                                                    dailyFrequency,zoneEntries, name, triageList);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    // Callback for triage incidents
    public interface TriageIncidentCallback {
        void onTriageIncidentsLoaded(ArrayList<String> triageList);
    }

    public ArrayList<String> getTriageIncidents(String childId, TriageIncidentCallback callback) {
        DatabaseReference triageRef = FirebaseDatabase.getInstance()
                .getReference("triage_incidents") // triage incidents folder
                .child(childId); // child id's folder

        ArrayList<String> triageList = new ArrayList<>();

        triageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot triageReceiver) {
                String chestStatus = null;
                String lipsStatus = null;
                String speakStatus = null;
                String triageIncident = null;

                for (DataSnapshot childSnap : triageReceiver.getChildren()) {
                    Boolean chest = childSnap.child("chest").getValue(Boolean.class);
                    String decision = childSnap.child("decision").getValue(String.class);
                    String guidance = childSnap.child("guidance").getValue(String.class);
                    Boolean lips = childSnap.child("lips").getValue(Boolean.class);
                    Boolean speak = childSnap.child("speak").getValue(Boolean.class);

                    if (chest) chestStatus = "☑";
                    else chestStatus = "☐";

                    if (lips) lipsStatus = "☑";
                    else lipsStatus = "☐";

                    if (speak) speakStatus = "☑";
                    else speakStatus = "☐";

                    triageIncident = chestStatus + "!!" + lipsStatus + "!!" +
                            speakStatus + "!!" + decision + "!!" + guidance;

                    triageList.add(triageIncident);
                }

                callback.onTriageIncidentsLoaded(triageList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return triageList;
    }
    public void generatePdf(Context context, String childId, int months, int[] dailyFrequency,
                            ArrayList<Entry> zoneEntries, String name, ArrayList<String> triageList) {

        // Creates an object for the PDF document
        PdfDocument document = new PdfDocument();

        // Creates a paint variable to use for writing text to the PDF
        Paint paint = new Paint();

        // Creates page dimensions, and number of pages
        // Letter size page
        PdfDocument.PageInfo pageInfo = new PdfDocument
                .PageInfo.Builder(612, 792, 1).create();

        // Creates start page
        PdfDocument.Page page = document.startPage(pageInfo);

        // Creates a canvas for the page
        Canvas canvas = page.getCanvas();

        paint.setColor(Color.BLACK);
        paint.setTextSize(24);
        paint.setFakeBoldText(true);

        canvas.drawText("Provider Report", 50, 50, paint);

        paint.setTextSize(20);
        canvas.drawText("Child: " + name, 50, 80, paint);
        canvas.drawText("Period: Last " + months + " Months", 50, 110, paint);

        canvas.drawText("Rescue Frequency (Usage By Day)", 50, 160, paint);

        // Rescue frequency, time series chart
        LineChart lineChart = new LineChart(context);
        lineChart.getDescription().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getXAxis().setDrawAxisLine(true);
        lineChart.getXAxis().setDrawLabels(true);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setTextSize(8f);
        lineChart.getAxisLeft().setTextSize(8f);
        lineChart.getLegend().setEnabled(false);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisLeft().setGranularity(1f); // one unit increments
        lineChart.getAxisLeft().setGranularityEnabled(true);

        ArrayList<Entry> entries = new ArrayList<>();

        for (int i = 0; i < dailyFrequency.length; i++) {
            entries.add(new Entry(i, dailyFrequency[i]));
        }

        LineDataSet lineDataSet = new LineDataSet(entries, "Rescue Frequency");
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setColor(Color.parseColor("#5C8AF2"));
        lineDataSet.setLineWidth(2f);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawValues(false);

        LineData lineData = new LineData(lineDataSet);

        lineChart.setData(lineData);

        int width = 500;
        int height = 300;

        lineChart.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));

        lineChart.layout(0, 0, width, height);

        Bitmap bitmap = createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas bitmapCanvas = new Canvas(bitmap);

        lineChart.draw(bitmapCanvas);
        canvas.drawBitmap(bitmap, 50, 150, null);
        // End of rescue frequency

        // Controller adherence, pie chart
        int plannedDays = 200; // To be calculated from db
        int takenDays = 125; // To be calculated from db
        int missedDays = plannedDays - takenDays;

        PieChart pieChart = new PieChart(context);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawEntryLabels(false);
        pieChart.getLegend().setEnabled(false);

        ArrayList<PieEntry> pieEntries = new ArrayList<>();

        pieEntries.add(new PieEntry((float)takenDays, "Taken"));
        pieEntries.add(new PieEntry((float)missedDays, "Missed"));

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Controller Adherence");
        pieDataSet.setColors(
                Color.parseColor("#5C8AF2"),
                Color.parseColor("#D3D3D3")); // Gray for missed
        pieDataSet.setValueTextSize(10f);
        pieDataSet.setValueTextColor(Color.WHITE);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);

        // Change floats to ints when displayed on the chart
        pieData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%.0f", value);
            }
        });

        int pieWidth = 300;
        int pieHeight = 300;

        pieChart.measure(View.MeasureSpec.makeMeasureSpec(pieWidth, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(pieHeight, View.MeasureSpec.EXACTLY));

        pieChart.layout(0, 0, pieWidth, pieHeight);

        Bitmap pieBitmap = createBitmap(pieWidth, pieHeight, Bitmap.Config.ARGB_8888);
        Canvas pieBitmapCanvas = new Canvas(pieBitmap);

        pieChart.draw(pieBitmapCanvas);

        canvas.drawText("Controller Adherence (Days Used/Missed)", 50, 490, paint);
        canvas.drawBitmap(pieBitmap, 50, 500, null);
        // End of controller adherence

        document.finishPage(page); // End of page 1

        // Create page 2
        PdfDocument.PageInfo pageInfo2 = new PdfDocument
                .PageInfo.Builder(612, 792, 2).create();
        PdfDocument.Page page2 = document.startPage(pageInfo2);
        Canvas canvas2 = page2.getCanvas();

        // Symptom burden
        int problemDays = 100; // To be calculated; may consider different kinds of problems

        canvas2.drawText("Symptom Burden (Problem Days)", 50, 50, paint);
        canvas2.drawText("Total Problem Days: " + problemDays, 50, 80, paint);
        // End of symptom burden

        // Zone distribution over time, time series chart
        LineChart zoneChart = new LineChart(context);
        zoneChart.getDescription().setEnabled(false);
        zoneChart.getAxisRight().setEnabled(false);
        zoneChart.getXAxis().setDrawAxisLine(true);
        zoneChart.getXAxis().setDrawLabels(true);
        zoneChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        zoneChart.getXAxis().setTextSize(8f);
        zoneChart.getXAxis().setAxisMinimum(0f);
        zoneChart.getAxisLeft().setTextSize(8f);
        zoneChart.getLegend().setEnabled(false);

        YAxis leftZoneAxis = zoneChart.getAxisLeft();
        leftZoneAxis.setAxisMinimum(0f);
        leftZoneAxis.setAxisMaximum(100f);
        zoneChart.getXAxis().setDrawGridLines(false);
        zoneChart.getAxisLeft().setDrawGridLines(false);
        zoneChart.getAxisLeft().setGranularity(10f); // 10 unit increments
        zoneChart.getAxisLeft().setGranularityEnabled(true);

        int totalDays = months * 30;

        float[] zoneValues = new float[totalDays];

        for (Entry entry : zoneEntries) {
            int dayIndex = (int) entry.getX();
            if (dayIndex >= 0 && dayIndex < totalDays) {
                zoneValues[dayIndex] = entry.getY();
            }
        }

        ArrayList<Entry> finalZoneEntries = new ArrayList<>();
        for (int i = 0; i < totalDays; i++) {
            finalZoneEntries.add(new Entry(i, zoneValues[i]));
        }
        // The above part ensures that all days that do not have a zone value will have
        // a zone value of 0, which will indicate that there is no data for that day

        zoneChart.getXAxis().setAxisMaximum(totalDays);

        LineDataSet zoneDataSet = new LineDataSet(finalZoneEntries, "Zone Distribution Over Time");
        zoneDataSet.setMode(LineDataSet.Mode.LINEAR); // Maybe linear
        zoneDataSet.setColor(Color.parseColor("#5C8AF2"));
        zoneDataSet.setLineWidth(2f);
        zoneDataSet.setDrawCircles(false);
        zoneDataSet.setDrawValues(false);

        LineData zoneData = new LineData(zoneDataSet);

        zoneChart.setData(zoneData);

        // Change floats to ints when displayed on the chart
        zoneData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        int zoneWidth = 500;
        int zoneHeight = 300;

        zoneChart.measure(View.MeasureSpec.makeMeasureSpec(zoneWidth, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(zoneHeight, View.MeasureSpec.EXACTLY));

        zoneChart.layout(0, 0, zoneWidth, zoneHeight);

        Bitmap zoneBitmap = createBitmap(zoneWidth, zoneHeight, Bitmap.Config.ARGB_8888);
        Canvas zoneBitmapCanvas = new Canvas(zoneBitmap);

        zoneChart.draw(zoneBitmapCanvas);
        canvas2.drawText("Zone Distribution Over Time (Zone By Day)", 50, 120, paint);
        canvas2.drawText("Zone Percentage Breakdown", 50, 150, paint);
        canvas2.drawText("  Green:      ≥80%", 50, 180, paint);
        canvas2.drawText("  Yellow:     50%-79%", 50, 210, paint);
        canvas2.drawText("  Red:          1%-50%", 50, 240, paint);
        canvas2.drawText("  No Data:   0%", 50, 270, paint);
        canvas2.drawBitmap(zoneBitmap, 50, 280, null);
        // End of zone distribution over time

        // Notable triage incidents, checklist chart
        if (!triageList.isEmpty()) {
            document.finishPage(page2); // End of page 2

            // Create page 3
            PdfDocument.PageInfo pageInfo3 = new PdfDocument
                    .PageInfo.Builder(612, 792, 2).create();
            PdfDocument.Page page3 = document.startPage(pageInfo2);
            Canvas canvas3 = page3.getCanvas();

            canvas3.drawText("Notable Triage Incidents", 50, 50, paint);

            paint.setTextSize(16);

            canvas3.drawText("Incident", 50, 80, paint);
            canvas3.drawText("Chest", 135, 80, paint);
            canvas3.drawText("Lips", 200, 80, paint);
            canvas3.drawText("Speak", 260, 80, paint);
            canvas3.drawText("Decision", 330, 80, paint);
            canvas3.drawText("Guidance", 460, 80, paint);

            int y = 110;

            for (int i = 0; i < triageList.size(); i++) {
                String triageIncident = triageList.get(i);

                String[] triageParts = triageIncident.split("!!");

                // add the associated check marks
                canvas3.drawText(String.valueOf(i + 1), 50, y, paint); // incident number
                canvas3.drawText(triageParts[0], 135, y, paint); // chest
                canvas3.drawText(triageParts[1], 200, y, paint); // lips
                canvas3.drawText(triageParts[2], 260, y, paint); // speak
                canvas3.drawText(triageParts[3], 330, y, paint); // decision
                canvas3.drawText(triageParts[4], 460, y, paint); // guidance

                y += 30;

                // If the page is full, continue creating new pages as needed

                if (y > 700) {
                    document.finishPage(page3);
                    page3 = document.startPage(pageInfo3); // same name so easier to close
                    canvas3 = page3.getCanvas();

                    y = 50;

                    canvas3.drawText("Incident", 50, 50, paint);
                    canvas3.drawText("Chest", 135, 50, paint);
                    canvas3.drawText("Lips", 200, 50, paint);
                    canvas3.drawText("Speak", 260, 50, paint);
                    canvas3.drawText("Decision", 330, 50, paint);
                    canvas3.drawText("Guidance", 460, 50, paint);

                    y = 80;
                }
            }

            document.finishPage(page3); // end of page 3
        }

        else {
            canvas2.drawText("No Notable Triage Incidents", 50, 620, paint);
            document.finishPage(page2); // End of page 2
        }

        // Download the pdf into the downloads folder
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment
                .DIRECTORY_DOWNLOADS), "Provider_Report.pdf");

        try {
            // Check if it writes the PDF
            document.writeTo(new FileOutputStream(file));

            Toast.makeText(context, "PDF file generated successfully.",
                    Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            // Handles error
            e.printStackTrace();
            Toast.makeText(context, "Failed to generate PDF.", Toast.LENGTH_SHORT).show();
        }

        document.close();
    }
}