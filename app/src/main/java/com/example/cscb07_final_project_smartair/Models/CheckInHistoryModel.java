package com.example.cscb07_final_project_smartair.Models;

import com.example.cscb07_final_project_smartair.DataObjects.CheckInData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;

public class CheckInHistoryModel {

    private FirebaseAuth mAuth;
    private final DatabaseReference mDatabase;

    public CheckInHistoryModel() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("check_in");
    }

    public interface OnSearchFinishedListener {
        void onSuccess(ArrayList<CheckInData> checkIns);
        void onFailure(String errorMessage);
    }


    public void browseCheckIns(OnSearchFinishedListener listener) {
        String userID = null;
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            listener.onFailure("User not logged in. Restart app.");
            return;
        }

        ArrayList<CheckInData> checkIns = new ArrayList<>();
        userID = currentUser.getUid();
        DatabaseReference checkinsRef = mDatabase.child(userID);
        checkinsRef.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot check_in_data) {
                if (!check_in_data.exists()) {
                    listener.onFailure("No check-ins found");
                    return;
                }
                for(DataSnapshot check_in_snapshot : check_in_data.getChildren()) {
                    CheckInData checkInData = check_in_snapshot.getValue(CheckInData.class);
                    if (checkInData != null) {
                        checkIns.add(checkInData);
                    }
                }
                listener.onSuccess(checkIns);
            }
            @Override
            public void onCancelled(@androidx.annotation.NonNull com.google.firebase.database.DatabaseError databaseError) {
                listener.onFailure(databaseError.getMessage());
            }

        });
    }

    public void filterCheckIns(ArrayList<String> symptoms, ArrayList<String> triggers, String[] date, OnSearchFinishedListener listener) {
        if(symptoms.isEmpty() && triggers.isEmpty() && date[0].isEmpty()){
            browseCheckIns(listener);
            return;
        }


        String userID = null;
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            listener.onFailure("User not logged in. Restart app.");
            return;
        }

        ArrayList<CheckInData> updatedCheckIns = new ArrayList<>();
        userID = currentUser.getUid();
        DatabaseReference checkinsRef = mDatabase.child(userID);
        checkinsRef.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot check_in_data) {

                int start_day=-1;
                int start_month=-1;
                int start_year=-1;
                int end_day=-1;
                int end_month=-1;
                int end_year=-1;


                if(!date[0].isEmpty()){
                    String[] start_date_components = date[0].split("/");
                    String[] end_date_components = date[1].split("/");

                    start_day = Integer.parseInt(start_date_components[0].trim());
                    start_month = Integer.parseInt(start_date_components[1].trim());
                    start_year = Integer.parseInt(start_date_components[2].trim());
                    end_day = Integer.parseInt(end_date_components[0].trim());
                    end_month = Integer.parseInt(end_date_components[1].trim());
                    end_year = Integer.parseInt(end_date_components[2].trim());

                }

                if (!check_in_data.exists()) {
                    listener.onFailure("No check-ins found");
                    return;
                }


                for(DataSnapshot check_in_snapshot : check_in_data.getChildren()) {
                    int matching_symptoms=0;
                    int matching_triggers=0;
                    int matching_date=0;
                    CheckInData checkInData = check_in_snapshot.getValue(CheckInData.class);
                    if (checkInData != null) {
                        for(String symptom : checkInData.symptoms){
                            if((symptoms.contains(symptom))){
                                matching_symptoms=1;
                                break;
                            }
                        }

                        for(String trigger : checkInData.triggers){
                            if((triggers.contains(trigger))){
                                matching_triggers=1;
                                break;
                            }
                        }

                        String[] check_in_date_components = checkInData.date.split("/");
                        int day = Integer.parseInt(check_in_date_components[0].trim());
                        int month = Integer.parseInt(check_in_date_components[1].trim());
                        int year = Integer.parseInt(check_in_date_components[2].trim());

                        if (start_year == end_year && year == start_year) {
                            if (start_month == end_month && month == start_month) {
                                if (start_day <= day && day <= end_day) {
                                    matching_date = 1;
                                }
                            }else if (start_month <= month && month <= end_month && (day >= start_day || day <= end_day)) {
                                    matching_date = 1;
                            }
                        } else if (start_year <= year && year <= end_year && (month >= start_month || month <= end_month) && (day >= start_day || day <= end_day)) {
                                matching_date = 1;
                        }

                        if (matching_symptoms == 1 || matching_triggers == 1 || matching_date == 1) {
                            updatedCheckIns.add(checkInData);
                        }
                    }
                }
                listener.onSuccess(updatedCheckIns);
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull com.google.firebase.database.DatabaseError databaseError) {
                listener.onFailure(databaseError.getMessage());
            }

        });
    }


}
