package com.example.cscb07_final_project_smartair.Presenters;

import com.example.cscb07_final_project_smartair.Views.RoleLauncherView;


public class RoleLauncherPresenter {

    private final RoleLauncherView view;

    public RoleLauncherPresenter(RoleLauncherView view) {
        this.view = view;
    }

    public void onChildClick() {
        view.navigateToChildLogin();
    }

    public void onParentClick() {
        view.navigateToGeneralLogin();
    }

    public void onProviderClick(){
        view.navigateToProviderLogin();
    }

}