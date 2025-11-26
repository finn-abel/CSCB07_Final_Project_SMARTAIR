package com.example.cscb07_final_project_smartair.Views;

import java.util.ArrayList;

public interface CheckInView {

    ArrayList<String> getTriggers();
    ArrayList<String> getOtherTriggers();
    ArrayList<String> getSymptoms();
    String getDate();
    void navigateToMainScreen();
    void showCheckInSuccess(String message);
    void showCheckInFailure(String message);




}
