package com.example.cscb07_final_project_smartair.Helpers;

import static android.graphics.Bitmap.createBitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ProviderReportGenerator {

    private DatabaseReference mdatabase;

    public ProviderReportGenerator() {
        mdatabase = FirebaseDatabase.getInstance().getReference();
    }
    public void generateReport(Context context, String childId, int months) {

        if (childId == null)
        {
            // Report if no child id is null
            Toast.makeText(context, "No child ID", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference rescueRef = FirebaseDatabase.getInstance()
                .getReference("users")  // user folder
                .child("children")  // children folder
                .child(childId)  // node matching child's id
                .child("rescueLogs"); // rescue logs of the child

        long now = System.currentTimeMillis();

        // Loads last 'months', 3 or 6, months
        // Converts milliseconds to months
        long startTime = now - ((long) months * 30 * 24 * 60 * 60 * 1000);

        ValueEventListener rescueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot receiver) {
                boolean dataExists = false;
                int[] dailyFrequency = new int[months * 31]; // Accounts for longer months

                for (DataSnapshot childSnap : receiver.getChildren()) {
                    Long timestamp = childSnap.child("timestamp").getValue(Long.class);

                    if (timestamp == null)
                    {
                        continue; // Skip iteration if the timestamp is null
                    }

                    if (timestamp <= startTime)
                    {
                        dataExists = true; // There is at least 'months' (3 or 6) months of data
                    }

                    if (timestamp >= startTime) {
                        // Find the day of the timestamp
                        int day = (int) ((now - timestamp) / ((long)24 * 60 * 60 * 1000));

                        // If valid day index
                        if (day >= 0 && day < dailyFrequency.length)
                        {
                            dailyFrequency[day]++;
                        }
                    }
                }

                if (!dataExists) {
                    Toast.makeText(context, "Not enough data", Toast.LENGTH_SHORT).show();
                    return;
                }

                generatePdf(context, childId, months, dailyFrequency);
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        };

        rescueRef.addListenerForSingleValueEvent(rescueListener);
    }

    public void generatePdf(Context context, String childId, int months, int[] dailyFrequency) {

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
        paint.setFakeBoldText(false);
        canvas.drawText("Child: " + childId, 50, 80, paint);
        canvas.drawText("Period: Last " + months + " Months", 50, 110, paint);

        paint.setFakeBoldText(true);
        canvas.drawText("Rescue Frequency", 50, 160, paint);

        // Time series chart
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

        document.finishPage(page);

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