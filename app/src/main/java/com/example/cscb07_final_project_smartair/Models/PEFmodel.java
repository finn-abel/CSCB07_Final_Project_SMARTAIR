package com.example.cscb07_final_project_smartair.Models;

import com.example.cscb07_final_project_smartair.DataObjects.pefCapture;
import com.example.cscb07_final_project_smartair.DataObjects.pefData;
import com.example.cscb07_final_project_smartair.Presenters.PEFPresenter;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PEFmodel {

    PEFPresenter presenter;
    public PEFmodel(PEFPresenter presenter){
        this.presenter = presenter;
    }

    public void logPEF(pefCapture capture){
        pefData pefLog = new pefData(
                capture.pre,
                capture.post,
                capture.current,
                capture.pb_pef
                );
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


    public interface getPBListener{
        void onPBRetrieved(float pb);
    }
    public void getPB(String childID, getPBListener listener){
        DatabaseReference path = FirebaseDatabase.getInstance()
                .getReference("users/children")
                .child(childID)
                .child("pb_pef");

        path.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if(!snapshot.exists()){
                            listener.onPBRetrieved(300F); //default value
                        } else {
                            Float pb = snapshot.getValue(Float.class);
                            if(pb == null){
                                listener.onPBRetrieved(pb);
                            } else{
                                listener.onPBRetrieved(300F);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });

    }
}
