package com.example.cscb07_final_project_smartair.Presenters;

import com.example.cscb07_final_project_smartair.Views.ProviderHomeView;

public class ProviderHomePresenter {
    private final ProviderHomeView view;
    //public ProviderHomeModel model;

    public ProviderHomePresenter(ProviderHomeView view){
        this.view = view;
        //this.model = new ProviderHomeModel();
    }


    public void onLogoutButtonClicked() {
        view.navigateToLoginScreen();
    }

    public void onProviderReportButtonClicked() {
        //navigate to provider report selection screen?
    }



}
