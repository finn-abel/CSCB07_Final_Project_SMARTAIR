package com.example.cscb07_final_project_smartair.Models;

import androidx.annotation.NonNull;

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
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public interface onCheckInFinishedListener {
        void onSuccess();

        void onFailure(String errorMessage);


    }

    public void submitCheckIn(ArrayList<String> symptoms, ArrayList<String> triggers, String date, CheckInModel.onCheckInFinishedListener listener) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        /*// Ensure a user is logged in before proceeding
        if (currentUser == null) {
            listener.onFailure("No user is currently logged in.");
            return;
        }*/

        String userId = currentUser.getUid();

        // Create a data structure to hold the check-in information
        HashMap<String, Object> checkInData = new HashMap<>();
        checkInData.put("date", date);
        checkInData.put("symptoms", symptoms);
        checkInData.put("triggers", triggers);

        // Upload the data to Firebase Realtime Database under the user's ID and the specific check-in date
        mDatabase.child("users").child(userId).child("checkIns").child(date)
                .setValue(checkInData).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            listener.onSuccess();
                        } else {
                            String errorMessage = task.getException() != null ?
                                    task.getException().getMessage() : "Failed to save data.";
                            listener.onFailure(errorMessage);
                        }
                    }
                });
    }
}
