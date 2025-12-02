package com.example.cscb07_final_project_smartair.Presenters;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.example.cscb07_final_project_smartair.Models.ParentHomeModel;
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

    private List<String> childIds = new ArrayList<>();
    private List<String> childNames = new ArrayList<>();

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
        if (index < 0 || index >= childIds.size()) return;

        String id = childIds.get(index);

        // send childId back to ParentHomeActivity //// change to right parenthomeactivity
        (view).setActiveChild(id);
    }

    public void loadChildrenDash() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child("children");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot receiver) {
                childIds.clear();
                childNames.clear();

                for (DataSnapshot child : receiver.getChildren()) {
                    childIds.add(child.getKey()); // get child's id
                    String name = child.child("name").getValue(String.class);

                    childNames.add(name);
                }

                view.displayChildren(childNames);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}