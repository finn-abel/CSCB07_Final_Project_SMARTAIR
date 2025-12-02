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

                int starting_date=0;
                int ending_date=0;


                String starting_day = "";
                String starting_month = "";
                String starting_year = "";
                String ending_day = "";
                String ending_month = "";
                String ending_year = "";


                if(date.length==2){
                    String[] start_date_components = date[0].split("/");
                    String[] end_date_components = date[1].split("/");

                    starting_day = start_date_components[0].trim();
                    starting_month = start_date_components[1].trim();
                    starting_year = start_date_components[2].trim();
                    ending_day = end_date_components[0].trim();
                    ending_month = end_date_components[1].trim();
                    ending_year = end_date_components[2].trim();
                    starting_date = Integer.parseInt(starting_year + starting_month + starting_day);
                    ending_date = Integer.parseInt(ending_year + ending_month + ending_day);
                }
                else if(date.length==1){
                    String[] date_components = date[0].split("/");
                    starting_day = date_components[0].trim();
                    ending_day = starting_day;
                    starting_month = date_components[1].trim();
                    ending_month = starting_month;
                    starting_year = date_components[2].trim();
                    ending_year =  starting_year;
                    starting_date = Integer.parseInt(starting_year + starting_month + starting_day);
                    ending_date = Integer.parseInt(ending_year + ending_month + ending_day);
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
                        String day = check_in_date_components[0].trim();
                        String month = check_in_date_components[1].trim();
                        String year = check_in_date_components[2].trim();
                        int date = Integer.parseInt(year + month + day);


                        if (starting_date <= date && date <= ending_date||starting_date == 0){
                            matching_date =1;
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
