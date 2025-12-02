package com.example.cscb07_final_project_smartair.Views;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cscb07_final_project_smartair.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.HashMap;

public class ProviderReportActivity extends BaseParentActivity {

    private DatabaseReference mdatabase;
    private String parentId;
    private HashMap<String, String> nameToIdMap = new HashMap<>();
    private String selectedChildId;

    private Spinner spinnerChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get a database instance
        mdatabase = FirebaseDatabase.getInstance().getReference();

        // Get parent ID from preferences
        // MODE_PRIVATE - Only accessible by calling app
        parentId = getSharedPreferences("APP_PREFS", MODE_PRIVATE)
                .getString("PARENT_ID", null);

        // ID found
        if (parentId != null) {
            createReport();
        }
    }

    public void createReport() {
        // Creates an object for the PDF document
        PdfDocument document = new PdfDocument();

        // Creates a paint variable to use for writing text to the PDF
        Paint paint = new Paint();

        // Creates page dimensions, and number of pages
        PdfDocument.PageInfo pageInfo = new PdfDocument
                .PageInfo.Builder(100, 100, 1).create();

        // Creates start page
        PdfDocument.Page page = document.startPage(pageInfo);

        // Creates a canvas for the page
        Canvas canvas = page.getCanvas();

        paint.setColor(Color.BLACK);
        paint.setTextSize(12);

        canvas.drawText("Provider Report", 50, 50, paint);
    }
}