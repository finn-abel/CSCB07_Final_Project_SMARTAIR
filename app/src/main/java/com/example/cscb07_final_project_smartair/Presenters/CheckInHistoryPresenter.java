package com.example.cscb07_final_project_smartair.Presenters;

import com.example.cscb07_final_project_smartair.Adapters.CheckInHistoryAdapter;
import com.example.cscb07_final_project_smartair.DataObjects.CheckInData;
import com.example.cscb07_final_project_smartair.Models.CheckInHistoryModel;
import com.example.cscb07_final_project_smartair.Models.CheckInModel;
import com.example.cscb07_final_project_smartair.Views.CheckInHistoryView;
import com.example.cscb07_final_project_smartair.Views.CheckInView;

import java.util.ArrayList;

public class CheckInHistoryPresenter implements CheckInHistoryModel.OnSearchFinishedListener{

    private CheckInHistoryModel model;
    private CheckInHistoryView view;


    public CheckInHistoryPresenter(CheckInHistoryView view) {
        this.view = view;
        this.model = new CheckInHistoryModel();
    }

    public void showCheckInHistory() {
        model.browseCheckIns(this);
    }

    public void onFilterButtonClicked() {
        ArrayList<String> symptoms = view.getSymptoms();
        ArrayList<String> triggers = view.getTriggers();
        String[] date = view.getDateRange();

        model.filterCheckIns(symptoms, triggers, date, this);
    }

    public void onExportButtonClicked() {
        ArrayList<CheckInData> checkIns = view.getCheckIns();
        view.generatePDF(checkIns);
    }

    @Override
    public void onSuccess(ArrayList<CheckInData> checkIns){
        if (view != null) {
            view.displayCheckInHistory(checkIns);
            view.showSearchSuccess("Search Successful!");
        }
    }

    @Override
    public void onFailure(String errorMessage){
        if (view != null) {
            view.showSearchFailure(errorMessage);
        }
    }


}
