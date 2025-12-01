package com.example.cscb07_final_project_smartair.Views;

import android.os.Bundle;
import android.os.Environment;
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
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class CheckInHistoryActivity extends BaseActivity implements CheckInHistoryView {

    private CheckInHistoryAdapter adapter;
    private RecyclerView recyclerView;
    private EditText symptomFilter;
    private EditText triggerFilter;
    private EditText dateRange;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin_history);

        CheckInHistoryPresenter presenter = new CheckInHistoryPresenter(this);
        recyclerView = findViewById(R.id.check_in_items);
        symptomFilter = findViewById(R.id.symptom_filter);
        triggerFilter = findViewById(R.id.trigger_filter);
        dateRange = findViewById(R.id.date_range);
        Button filter_button = findViewById(R.id.filter_button);
        Button pdf_export = findViewById(R.id.pdf_export);

        setupRecyclerView();

        presenter.showCheckInHistory();

        filter_button.setOnClickListener(v -> {
            presenter.onFilterButtonClicked();
        });

        pdf_export.setOnClickListener(v -> {
            presenter.onExportButtonClicked();
        });


    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CheckInHistoryAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
    }
    @Override
    public ArrayList<CheckInData> getCheckIns() {
        return adapter.onScreenData();
    }


    @Override
    public void generatePDF(ArrayList<CheckInData> checkInEntries) {
        File pdfDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        String uniqueStamp = LocalDateTime.now().toString().replace(":", "_");
        String pdfPath = pdfDirectory.toString() + "/CheckInHistory_" + uniqueStamp + ".pdf";

        PdfFont boldFont = null;
        PdfFont regularFont = null;
        PdfFont italicFont = null;
        PdfFont boldItalicFont = null;
        PdfWriter writer = null;
        Document exportDoc = null;

        try {
            regularFont = PdfFontFactory.createFont(StandardFonts.COURIER);
            boldFont = PdfFontFactory.createFont(StandardFonts.COURIER_BOLD);
            italicFont = PdfFontFactory.createFont(StandardFonts.COURIER_OBLIQUE);
            boldItalicFont = PdfFontFactory.createFont(StandardFonts.COURIER_BOLDOBLIQUE);
            File pdfFile = new File(pdfPath);
            writer = new PdfWriter(pdfFile);
            PdfDocument exportPDF = new PdfDocument(writer);
            exportDoc = new Document(exportPDF);
        } catch (IOException e) {
            String errorMessage = e.getMessage();
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }

        if ((exportDoc != null) && boldFont!=null && regularFont!=null && italicFont!=null && boldItalicFont!=null) {
            exportDoc.add(new Paragraph("CHECK-IN HISTORY").setTextAlignment(TextAlignment.CENTER)
                    .setFont(boldFont).setFontSize(26));


            exportDoc.add(new Paragraph("\n"));

            if(checkInEntries.isEmpty()) {
                exportDoc.add(new Paragraph("No check-ins found.").setFont(regularFont).setFontSize(16));
            }


            for (CheckInData submission : checkInEntries) {
                exportDoc.add(new Paragraph("Date: " + submission.date).setFont(boldFont).setFontSize(16));
                exportDoc.add(new Paragraph("\n"));
                exportDoc.add(new Paragraph("Symptoms: " + String.join(", ", submission.symptoms)).setFont(regularFont).setFontSize(14));
                exportDoc.add(new Paragraph("\n"));
                exportDoc.add(new Paragraph("Triggers: " + String.join(", ", submission.triggers)).setFont(regularFont).setFontSize(14));
                exportDoc.add(new Paragraph("\n"));
            }

            exportDoc.close();
            Toast.makeText(this, "PDF created and saved to Files.", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Error creating PDF.", Toast.LENGTH_SHORT).show();
        }

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



