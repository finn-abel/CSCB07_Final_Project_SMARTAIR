package com.example.cscb07_final_project_smartair.Models;

import androidx.annotation.NonNull;

import com.example.cscb07_final_project_smartair.DataObjects.triageCapture;
import com.example.cscb07_final_project_smartair.DataObjects.triageData;
import com.example.cscb07_final_project_smartair.Presenters.TriagePresenter;
import com.example.cscb07_final_project_smartair.Users.ChildSpinnerOption;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TriageModel {

    public TriagePresenter Tpresenter;

    public int rescue_count;

    public TriageModel(TriagePresenter presenter){
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

    public interface ChildFetchListener { //listener interface
        void onChildrenLoaded(List<ChildSpinnerOption> children);
        void onError(String message);
    }

    public void fetchChildren(String userID, ChildFetchListener listener) { //get children for spinner
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();

        root.child("users/parents").child(userID).child("children") //get path
                .addListenerForSingleValueEvent(new ValueEventListener() { //read once
                    @Override
                    public void onDataChange(DataSnapshot childListSnap) {
                        if (!childListSnap.exists()) {
                            listener.onChildrenLoaded(new ArrayList<>()); // empty list on no children
                            return;
                        }


                        List<ChildSpinnerOption> childrenList = new ArrayList<>();   //list for kids
                        long totalChildren = childListSnap.getChildrenCount();
                        final int [] count = {0};    //so lambdas can increment

                        // 2. Loop through every ID
                        for (DataSnapshot childKey : childListSnap.getChildren()) {  //loop through children in list
                            String childID = childKey.getKey();

                            // 3. Go fetch the Name for this ID
                            root.child("users/children").child(childID).child("fullName")
                                    .get().addOnSuccessListener(nameSnap -> {

                                        String name = nameSnap.getValue(String.class);
                                        childrenList.add(new ChildSpinnerOption(childID, name));
                                        count[0]++;  //increment

                                        if (count[0] == totalChildren) {
                                            listener.onChildrenLoaded(childrenList); //send full list
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        listener.onError(error.getMessage());
                    }
                });
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

}
