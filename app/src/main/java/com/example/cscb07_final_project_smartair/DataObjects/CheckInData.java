package com.example.cscb07_final_project_smartair.DataObjects;

import android.os.Parcelable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class CheckInData extends Data {
    public ArrayList<String> symptoms;
    public ArrayList<String> triggers;
    public String date;
    public String authorRole;

    public CheckInData(){}

    public CheckInData(ArrayList<String> symptoms, ArrayList<String> triggers, String email, String date){
        super(System.currentTimeMillis(), FirebaseAuth.getInstance().getCurrentUser().getUid(),"Daily Check In");
        this.symptoms = symptoms;
        this.triggers = triggers;
        this.date = date;

        if(email.contains("@smartair.com")){
            this.authorRole = "Child";
        } else {
            this.authorRole = "Parent";}
    }
}



