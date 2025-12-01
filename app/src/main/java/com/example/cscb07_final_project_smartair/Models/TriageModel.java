package com.example.cscb07_final_project_smartair.Models;

import androidx.annotation.NonNull;

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
                .getReference("medicine/rescue")
                .child(childID);

        path.orderByChild("timestamp").startAt(threeHoursAgo) //last three house
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        rescue_count = (int) snapshot.getChildrenCount(); //get count
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        rescue_count = 0; // zero on error
                    }
                });
    }

    public boolean isRedFlag(triageCapture capture) {
        boolean physicalRedFlags = (capture.speak || capture.lips || capture.chest);

        boolean rapidRescueFlag = false;
        boolean pefConcern = false;
        if (capture.shared_rescue) {
            rapidRescueFlag = (this.rescue_count >= 3);
        }

        if(capture.pef != null){
            pefConcern = (capture.pef <=40); //change to red-zone when implemented
        }

        return physicalRedFlags || rapidRescueFlag || pefConcern;
    }

    public interface ImprovementListener {
        void onComparisonComplete(boolean shouldEscalate);
    }

    public void isImprovement(triageCapture capture, ImprovementListener listener) {
        String childID = capture.userID;
        DatabaseReference path = FirebaseDatabase.getInstance()
                .getReference("triage_incidents")
                .child(childID);

        path.orderByKey().limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (!snapshot.exists()) { //no history
                            listener.onComparisonComplete(isRedFlag(capture));
                            //default to current flags
                            return;
                        }

                        triageData lastLog = null;
                        for (DataSnapshot child : snapshot.getChildren()) {
                            lastLog = child.getValue(triageData.class);
                        } // get last log

                        if (lastLog == null) {
                            listener.onComparisonComplete(isRedFlag(capture));
                            //default to current flags on error
                            return;
                        }
                        boolean prePef = false;
                        boolean currPef = false;
                        if(lastLog.pef != null) { prePef = lastLog.pef <40;}
                        if(capture.pef != null) { currPef = capture.pef <40;}

                        //compare flags before and now
                        boolean prev = (lastLog.speak || lastLog.lips || lastLog.chest
                                || prePef );
                        boolean curr = (capture.speak || capture.lips || capture.chest
                                ||currPef );

                        boolean escalate = false;

                        if (curr) {
                            escalate = true;
                        } else if (prev && !curr) {
                            escalate = false;
                        }

                        // 5. Return decision
                        listener.onComparisonComplete(escalate);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        listener.onComparisonComplete(isRedFlag(capture));
                    }
                });
    }

    public interface getRemedyListener {
        void onRemedyRetrieved(String s, String level);
    }

    public void getRemedy(triageCapture capture, getRemedyListener listener){
        DatabaseReference path = FirebaseDatabase.getInstance()
                .getReference("users/children")
                .child(capture.userID);

        path.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (!snapshot.exists()) { //no history
                            listener.onRemedyRetrieved("No guidance", "Green");
                            //default to current flags
                            return;
                        }

                        Child childProfile = snapshot.getValue(Child.class);

                        // Safety check (in case the data was malformed)
                        if (childProfile != null) {
                            if (capture.pef < (0.5 * childProfile.pb_pef)) {
                                listener.onRemedyRetrieved(childProfile.pef_guidance.red, "RED");
                            } else if (capture.pef <= (0.8 * childProfile.pb_pef)) {
                                listener.onRemedyRetrieved(childProfile.pef_guidance.yellow, "YELLOW");
                            } else {
                                listener.onRemedyRetrieved(childProfile.pef_guidance.green, "GREEN");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });

    }

}
