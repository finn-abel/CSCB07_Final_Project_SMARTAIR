package com.example.cscb07_final_project_smartair.Models;

import com.example.cscb07_final_project_smartair.DataObjects.pefData;
import com.example.cscb07_final_project_smartair.Presenters.PEFPresenter;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PEFmodel {

    PEFPresenter presenter;

    public PEFmodel(PEFPresenter presenter){
        this.presenter = presenter;
    }
    public void logPEF(Float pre, Float post, Float current){
        pefData pefLog = new pefData(pre,post,current);
        String childID = null;
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) { //user not found
            presenter.onLogFailure("User not logged in. Restart app.");
            return;
        }
        childID = user.getUid();
        DatabaseReference logsRef = FirebaseDatabase.getInstance()
                .getReference("pef")
                .child(childID);
        String logID = logsRef.push().getKey(); //get time-ordered key
        if(logID!=null) {
            logsRef.child(logID).setValue(pefLog)
                    .addOnSuccessListener(aVoid -> {
                        presenter.onLogSuccess();
                    })
                    .addOnFailureListener( e -> {
                        presenter.onLogFailure(e.getMessage());
                    });
        } else {
            presenter.onLogFailure("Failed to generate LogID");
        }
    }
}
