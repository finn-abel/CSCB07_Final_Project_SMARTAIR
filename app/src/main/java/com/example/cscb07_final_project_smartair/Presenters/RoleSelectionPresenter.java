package com.example.cscb07_final_project_smartair.Presenters;

import com.example.cscb07_final_project_smartair.Views.RoleSelectionView;

public class RoleSelectionPresenter {

    private RoleSelectionView view;

    public RoleSelectionPresenter(RoleSelectionView view) {
        this.view = view;
    }

    public void onChildClick() {
        view.navigateToChildHome();
    }

    public void onParentClick() {
        view.navigateToParentHome();
    }

    public void onProviderClick() {
        view.navigateToProviderHome();
    }
}