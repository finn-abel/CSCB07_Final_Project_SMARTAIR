package com.example.cscb07_final_project_smartair.Presenters;

import com.example.cscb07_final_project_smartair.Models.CheckInModel;
import com.example.cscb07_final_project_smartair.Views.CheckInView;

import java.util.ArrayList;

public class CheckInPresenter implements CheckInModel.onCheckInFinishedListener{

    private CheckInModel model;
    private CheckInView view;

    public CheckInPresenter(CheckInView view) {
        this.view = view;
        this.model = new CheckInModel();
    }

    public void onSubmitButtonClicked() {

        ArrayList<String> symptoms = view.getSymptoms();
        ArrayList<String> triggers = view.getTriggers();
        String date = view.getDate();

        model.submitCheckIn(symptoms, triggers, date, this);
    }

    @Override
    public void onSuccess(){
        if (view != null) {
            view.showCheckInSuccess("Daily CheckIn Successfully Completed!");
            view.navigateToMainScreen();
        }
    }

    @Override
    public void onFailure(String errorMessage){
        if (view != null) {
            view.showCheckInFailure(errorMessage);
        }
    }


}
