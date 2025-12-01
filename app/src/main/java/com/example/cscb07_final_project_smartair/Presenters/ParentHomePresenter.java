package com.example.cscb07_final_project_smartair.Presenters;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.cscb07_final_project_smartair.Models.ParentHomeModel;
import com.example.cscb07_final_project_smartair.Views.ParentHomeView;

public class ParentHomePresenter{

    private final ParentHomeView view;
    public ParentHomeModel model;

    public ParentHomePresenter(ParentHomeView view){
        this.view = view;
        this.model = new ParentHomeModel();
    }


    public void onLogoutButtonClicked() {

        SharedPreferences prefs = view.getContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        prefs.edit().clear().apply(); //clear preferences

        model.signOut();

        view.navigateToLoginScreen();
    }

    public void onScheduleButtonClicked(){
        view.navigateToSchedule();
    }
    public void onPEFButtonClicked(){
        view.navigateToPEFEntry();
    }

    public void onCheckInButtonClicked() {
        view.navigateToCheckInScreen();
    }

    public void onMedicineLogsClicked() {
        view.navigateToMedicineLogs();
    }

    public void onInventoryClicked() {
        view.navigateToInventory();
    }

    public void onProviderReportClicked() { view.navigateToProviderReport(); }

    public void onCheckInHistoryClicked() {
        view.navigateToCheckInHistoryScreen();
    }
    public void onBadgeSettingsClicked() {
        view.navigateToBadgeSettings();
    }
}