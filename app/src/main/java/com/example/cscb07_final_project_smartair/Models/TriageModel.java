package com.example.cscb07_final_project_smartair.Models;

import androidx.annotation.NonNull;

import com.example.cscb07_final_project_smartair.DataObjects.pefData;
import com.example.cscb07_final_project_smartair.DataObjects.triageCapture;
import com.example.cscb07_final_project_smartair.DataObjects.triageData;
import com.example.cscb07_final_project_smartair.Presenters.TriagePresenter;
import com.example.cscb07_final_project_smartair.Users.Child;
import com.example.cscb07_final_project_smartair.Users.ChildSpinnerOption;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TriageModel extends BaseModel{

    public TriagePresenter Tpresenter;

    public int rescue_count;
    public float cachedPB = -1;
    public Float cachedRecentPef = null;
    //load on selection to avoid simultaneous db pulls
    public TriageModel(TriagePresenter presenter){
        super();
        this.Tpresenter = presenter;
    }// constructor

    public void logIncident(String decision, String guidance, triageCapture capture, boolean escalation){
        triageData triageLog = new triageData(decision,
                guidance,
                capture.speak,
                capture.lips,
                capture.chest,
                capture.pef,
                escalation,
                rescue_count);

        //find decision type based on pef and decision == remedy or emergency

        DatabaseReference logsRef = FirebaseDatabase.getInstance()
                .getReference("triage_incidents")
                .child(capture.userID);
        String logID = logsRef.push().getKey(); //get time-ordered key
        if(logID!=null) {
            logsRef.child(logID).setValue(triageLog)
                    .addOnSuccessListener(aVoid -> {
                        Tpresenter.onLogSuccess();
                    })
                    .addOnFailureListener( e -> {
                        Tpresenter.onLogFailure(e.getMessage());
                    });
        } else {
            Tpresenter.onLogFailure("Failed to generate LogID");
        }
    }

    public void preloadRescueLogs(String childID) {
        long threeHoursAgo = System.currentTimeMillis() - (3 * 60 * 60 * 1000); //3 hours prior

        DatabaseReference path = FirebaseDatabase.getInstance()
                .getReference("users/children")
                .child(childID)
                .child("medicine")
                .child("rescue");

        path.orderByChild("timestamp").startAt(threeHoursAgo)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        rescue_count = (int) snapshot.getChildrenCount();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        rescue_count = 0;
                    }
                });
    }

    public void preloadData(String childID) {
        //load rescue logs
        preloadRescueLogs(childID);


        //load pb for pef
        DatabaseReference childRef = FirebaseDatabase.getInstance()
                .getReference("users/children").child(childID);

        childRef.child("pb_pef").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Assuming stored as Integer or Float in DB
                    cachedPB = snapshot.getValue(Float.class);
                } else {
                    cachedPB = -1; // Not set
                }
            }
            @Override public void onCancelled(DatabaseError error) {}
        });

        //find most recent pef in last 24hrs
        long oneDayAgo = System.currentTimeMillis() - (24 * 60 * 60 * 1000);
        DatabaseReference pefRef = FirebaseDatabase.getInstance()
                .getReference("users/chidren").child(childID);

        pefRef.orderByChild("timestamp").startAt(oneDayAgo).limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        cachedRecentPef = null;
                        for (DataSnapshot child : snapshot.getChildren()) {
                            cachedRecentPef = child.child("current").getValue(Float.class);
                        }
                    }
                    @Override public void onCancelled(DatabaseError error) {}
                });
    }

    public boolean isRedZone(Float pefValue) {
        // when no recent PEF and no pb
        if (pefValue == null || cachedPB <= 0) {
            return false;
        }
        return (pefValue < (0.5f * cachedPB));
    }

    public boolean isRedFlag(triageCapture capture) {
        boolean physicalRedFlags = (capture.speak || capture.lips || capture.chest);

        boolean rapidRescueFlag = false;
        if (capture.shared_rescue) {
            rapidRescueFlag = (this.rescue_count >= 3);
        }
        Float pefToCheck = (capture.pef != null) ? capture.pef : cachedRecentPef; // get relevant value
        boolean pefRedZone = isRedZone(pefToCheck);
        return physicalRedFlags || rapidRescueFlag || pefRedZone;
    }

    public interface getRemedyListener {
        void onRemedyRetrieved(String s, String level);
    }
    public void getRemedy(triageCapture capture, getRemedyListener listener) {

        if (capture.pef != null) { //user gave PEF
            fetchProfileZone(capture.userID, capture.pef, listener);
        }
        else {  //get most recent PEF in last day
            long now = System.currentTimeMillis();
            long oneDayAgo = now - (24 * 60 * 60 * 1000);

            DatabaseReference logsRef = FirebaseDatabase.getInstance()
                    .getReference("pef")
                    .child(capture.userID);

            logsRef.orderByChild("timestamp")
                    .startAt(oneDayAgo)
                    .limitToLast(1)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (!snapshot.exists()) {
                                listener.onRemedyRetrieved("No recent Peak Flow data", "NONE");
                                return;
                            }
                            for (DataSnapshot child : snapshot.getChildren()) {
                                Integer recentPef = child.child("current").getValue(Integer.class);
                                //get latest pef
                                if (recentPef != null) {
                                    fetchProfileZone(capture.userID, (float) recentPef, listener);
                                } else {
                                    listener.onRemedyRetrieved("Invalid data found", "NONE");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            listener.onRemedyRetrieved("Error fetching logs", "NONE");
                        }
                    });
        }
    }
    public void fetchProfileZone(String userID, float pefValue, getRemedyListener listener) {
        DatabaseReference path = FirebaseDatabase.getInstance()
                .getReference("users/children")
                .child(userID);

        path.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    listener.onRemedyRetrieved("None", "NONE");
                    return;
                }
                Child childProfile = snapshot.getValue(Child.class);

                if (childProfile != null) {

                    if (pefValue < (0.5 * childProfile.pb_pef)) {
                        listener.onRemedyRetrieved(childProfile.pef_guidance.red, "RED");
                    } else if (pefValue < (0.8 * childProfile.pb_pef)) {
                        listener.onRemedyRetrieved(childProfile.pef_guidance.yellow, "YELLOW");
                    } else {
                        listener.onRemedyRetrieved(childProfile.pef_guidance.green, "GREEN");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                listener.onRemedyRetrieved("Database Error", "Grey");
            }
        });
    }

}
