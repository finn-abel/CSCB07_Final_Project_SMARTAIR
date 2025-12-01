package com.example.cscb07_final_project_smartair.Models;

import com.example.cscb07_final_project_smartair.DataObjects.pefCapture;
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

    public void logPEF(pefCapture capture){
        pefData pefLog = new pefData(
                capture.pre,
                capture.post,
                capture.current);
        DatabaseReference logsRef = FirebaseDatabase.getInstance()
                .getReference("pef")
                .child(capture.childID);
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
