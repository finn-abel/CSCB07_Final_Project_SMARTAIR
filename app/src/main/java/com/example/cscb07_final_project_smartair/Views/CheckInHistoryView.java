package com.example.cscb07_final_project_smartair.Views;

import com.example.cscb07_final_project_smartair.DataObjects.CheckInData;


import java.util.ArrayList;

public interface CheckInHistoryView {

    void showSearchSuccess(String message);
    void displayCheckInHistory(ArrayList<CheckInData> checkIns);

    void showSearchFailure(String message);

    ArrayList<String> getSymptoms();

    ArrayList<String> getTriggers();

    String[] getDateRange();
    public void generatePDF(ArrayList<CheckInData> checkInEntries);
    public ArrayList<CheckInData> getCheckIns();
}
