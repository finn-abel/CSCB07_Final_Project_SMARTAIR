package com.example.cscb07_final_project_smartair.Presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.cscb07_final_project_smartair.Models.BaseModel;
import com.example.cscb07_final_project_smartair.Models.ParentHomeModel;
import com.example.cscb07_final_project_smartair.Users.ChildSpinnerOption;
import com.example.cscb07_final_project_smartair.Views.ParentHomeActivity;
import com.example.cscb07_final_project_smartair.Views.ParentHomeView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ParentHomePresenter{

    private final ParentHomeView view;
    public ParentHomeModel model;
    private List<ChildSpinnerOption> children = new ArrayList<>();

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

    public void onManageChildrenClicked() {
        view.navigateToManageChildren();
    }

    public void onChildSelectedDash(int index) {
        if (index < 0 || index >= children.size()) return;

        ChildSpinnerOption selected = children.get(index);
        view.setActiveChild(selected.userID);
    }

    public void loadChildrenDash() {

        String parentId = model.getCurrentUserId();

        model.fetchChildren(parentId, new BaseModel.ChildFetchListener() {

            @Override
            public void onChildrenLoaded(List<ChildSpinnerOption> childList) {
                children = childList;
                view.displayChildren(childList);
            }

            @Override
            public void onError(String message) {
                // Optional: handle errors, e.g. Toast
            }
        });
    }
}