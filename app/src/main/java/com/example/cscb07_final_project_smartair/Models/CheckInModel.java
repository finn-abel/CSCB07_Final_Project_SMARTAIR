package com.example.cscb07_final_project_smartair.Models;

import static kotlinx.coroutines.internal.Concurrent_commonKt.setValue;

import androidx.annotation.NonNull;

import com.example.cscb07_final_project_smartair.DataObjects.CheckInData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseUser;
import java.util.HashMap;
import java.util.Map;

public class CheckInModel {

    private FirebaseAuth mAuth;
    private final DatabaseReference mDatabase;

    public CheckInModel() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("check_in");
    }

    public interface onCheckInFinishedListener {
        void onSuccess();

        void onFailure(String errorMessage);


    }

    public void submitCheckIn(ArrayList<String> symptoms, ArrayList<String> triggers, String date,
                              onCheckInFinishedListener listener) {
        String userID = null;
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            listener.onFailure("User not logged in. Restart app.");
            return;
        }

        //Creating object to store check-in info in database
        String email = currentUser.getEmail();
        CheckInData checkInData = new CheckInData(symptoms, triggers, email, date);

        userID = currentUser.getUid();



        // Upload the data to Firebase
        DatabaseReference logsRef = mDatabase.child(userID);
        String logID = logsRef.push().getKey(); //get time-ordered key
        if(logID!=null) {
            logsRef.child(logID).setValue(checkInData).addOnSuccessListener(aVoid -> {
                listener.onSuccess();
            }).addOnFailureListener(e -> {
                listener.onFailure(e.getMessage());
            });
        }
        else {
            listener.onFailure("Failed to generate LogID");
        }
    }
}
