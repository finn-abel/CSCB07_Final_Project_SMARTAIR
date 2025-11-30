package com.example.cscb07_final_project_smartair.Views;

import com.example.cscb07_final_project_smartair.DataObjects.triageCapture;
import com.example.cscb07_final_project_smartair.Users.ChildSpinnerOption;

import java.util.List;

public interface TriageView {
    void callTriage(boolean recheck, String childID);

    void showCheckup(String childID);

    void showDecision(boolean isRedFlag);

    void onFormSubmit(triageCapture capture);//submit form

    void showSteps();

    void closeDialog();

    void showTimerStart(triageCapture capture, long n);

    void callEmergency(triageCapture capture, boolean escalation);

    void createNotificationChannel();

    void showLogTriageSuccess();

    void showLogTriageFailure(String s);


    boolean isParent();

    void showError(String message);;

    void getChildren();

    void passChildrenToFragment(List<ChildSpinnerOption> children);

    void onChildSelected(String childID);
}
