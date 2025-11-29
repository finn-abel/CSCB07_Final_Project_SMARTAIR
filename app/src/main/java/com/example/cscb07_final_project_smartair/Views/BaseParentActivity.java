package com.example.cscb07_final_project_smartair.Views;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cscb07_final_project_smartair.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class BaseParentActivity extends BaseActivity {

    public String parentId;
    public String activeChildId = null;
    public DatabaseReference mdatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get a database instance
        mdatabase = FirebaseDatabase.getInstance().getReference();

        // Get parent ID from preferences
        // MODE_PRIVATE - Only accessible by calling app
        parentId = getSharedPreferences("APP_PREFS", MODE_PRIVATE)
                .getString("PARENT_ID", null);

        if (parentId != null)
        {
            alertListeners();
        }
    }

    private void alertListeners() {
        listenForChildrenAlerts();
    }

    // Listens to alerts for each child
    private void listenForChildrenAlerts() {

        ValueEventListener childListener = new ValueEventListener() {

            // Iterates through all children and runs whenever child/data is added,
            // removed or updated
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnap : dataSnapshot.getChildren()) {

                    // Gets current child's ID so activeChildId is never null
                    String childId = childSnap.getKey();
                    if (childSnap.hasChild(parentId)) {

                        if (activeChildId == null) {
                            activeChildId = childId;
                        }

                        // Listeners for each alert category
                        listenTriage(childId);
                        listenTodayZone(childId);
                        listenRapidRescue(childId);
                        listenInventory(childId);
                    }
                }
            }

            // Handles read errors
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ParentListener", "Failed to read children",
                        databaseError.toException());
            }
        };

        // Listens to the data in the 'parentsByChild' node
        DatabaseReference childrenRef = mdatabase.child("parentsByChild");
        childrenRef.addValueEventListener(childListener);
    }

    // Today zone listener, alert for the child's zone today
    // Listens to the child based on the ID passed as an argument
    private void listenTodayZone(String childId) {
        DatabaseReference zoneRef =
                mdatabase.child("children").child(childId).child("todayZone");

        ValueEventListener zoneListener = new ValueEventListener() {

            // Runs whenever data under today's zone changes
            @Override
            public void onDataChange(@NonNull DataSnapshot receiver) {
                String zone = receiver.getValue(String.class);

                // Notifies the parent if the zone is red
                if ("red".equals(zone)) {
                    showAlert("Red Zone Alert",
                            "Your child is in the RED asthma zone today.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };

        zoneRef.addValueEventListener(zoneListener);
    }

    // Rapid rescue repeats listener, listens for rescue uses
    private void listenRapidRescue(String childId) {
        DatabaseReference logsRef =
                mdatabase.child("children").child(childId).child("rescueLogs");

        ChildEventListener logsListener = new ChildEventListener() {

            // Runs when a new rescue log is added
            @Override
            public void onChildAdded(@NonNull DataSnapshot childSnap, String previousChildName) {

                ValueEventListener rescueListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot parentSnap) {
                        long now = System.currentTimeMillis();
                        int count = 0;
                        for (DataSnapshot childSnap : parentSnap.getChildren()) {
                            Long timestamp = childSnap.child("timestamp").getValue(Long.class);

                            // Between now and 3 hours ago
                            // Convert milliseconds to hours
                            if (timestamp != null && timestamp >= now - 3*60*60*1000) {
                                count++;
                            }
                        }

                        // Alerts if 3 or more rescue uses in the last 3 hours
                        if (count >= 3) {
                            showAlert("Rescue Inhaler Alert",
                                    "Your child has used rescue inhaler 3 times in 3 hours.");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                };

                // Reads all rescue logs of a child
                Objects.requireNonNull(childSnap.
                        getRef().getParent()).addListenerForSingleValueEvent(rescueListener);
            }

            // Following functions are not needed
            // Detects if a rescue time has changed
            @Override
            public void onChildChanged(@NonNull DataSnapshot childSnap,
                                       String previousChildName) {}

            // Detects if a rescue time has been removed
            @Override
            public void onChildRemoved(@NonNull DataSnapshot childSnap) {}

            // Detects if the order of rescue logs has changed
            @Override
            public void onChildMoved(@NonNull DataSnapshot childSnap,
                                     String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };

        logsRef.addChildEventListener(logsListener);
    }

    // Worse after dose listener, checks and alerts if the child is feeling worse after a dose
    private void listenWorseAfterDose(String childId) {
        DatabaseReference doseRef =
                mdatabase.child("children").child(childId).child("doseChecks");

        ChildEventListener childListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot receiver, String previousChildName) {

                // Gets the child's condition after the dose and stores it in 'after'
                String after = receiver.child("after").getValue(String.class);

                // If worse, alert the parent
                if ("Worse".equals(after)) {
                    showAlert("Dose Check: Worse",
                            "Your child reported feeling worse after their dose.");
                }
            }

            // Following functions are not needed
            @Override public void onChildChanged(@NonNull DataSnapshot childSnap, String previousChildName) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot childSnap) {}
            @Override public void onChildMoved(@NonNull DataSnapshot childSnap, String previousChildName) {}
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        };

        doseRef.addChildEventListener(childListener);
    }

    // Triage escalation listener
    private void listenTriage(String childId) {
        DatabaseReference triageRef =
                mdatabase.child("children").child(childId).child("triage");

        ChildEventListener childListener = new ChildEventListener() {

            // After a new triage session, checks if escalated
            @Override
            public void onChildAdded(@NonNull DataSnapshot receiver, String previousChildName) {

                // Finds whether escalated is true or false
                Boolean escalated = receiver.child("escalated").getValue(Boolean.class);
                if (Boolean.TRUE.equals(escalated)) {
                    showAlert("Triage Escalation",
                            "Your child's triage has escalated.");
                }
            }

            // Following functions are not needed
            @Override public void onChildChanged(@NonNull DataSnapshot receiver, String previousChildName) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot receiver) {}
            @Override public void onChildMoved(@NonNull DataSnapshot receiver, String previousChildName) {}
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        };

        triageRef.addChildEventListener(childListener);
    }

    // Inventory low/expired listener, alerts the parent if the child's medication is low
    // or expired
    private void listenInventory(String childId) {
        DatabaseReference inventoryRef =
                mdatabase.child("children").child(childId).child("inventory");

        ChildEventListener childListener = new ChildEventListener() {

            // Runs if a new medication is added
            @Override
            public void onChildAdded(@NonNull DataSnapshot receiver, String previousChildName) {
                checkInventory(receiver);
            }

            // Runs anytime the medication level changes or the medication becomes expired
            @Override
            public void onChildChanged(@NonNull DataSnapshot receiver, String previousChildName) {
                checkInventory(receiver);
            }

            // Following function are not needed
            @Override
            public void onChildRemoved(@NonNull DataSnapshot receiver) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot receiver, String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}

            // Checks whether the medicine's low or expired indicators are true and alerts
            // the parent accordingly
            private void checkInventory(DataSnapshot receiver) {

                Boolean expired = receiver.child("expired").getValue(Boolean.class);
                Boolean low = receiver.child("low").getValue(Boolean.class);
                if (Boolean.TRUE.equals(expired)) {
                    showAlert("Expired Medication",
                            "Your child's medication has expired.");
                }
                if (Boolean.TRUE.equals(low)) {
                    showAlert("Low Inhaler", "Your child's inhaler is low.");
                }
            }
        };

        inventoryRef.addChildEventListener(childListener);
    }

    // Displays a popup method for all the alerts on the parent's app
    private void showAlert(String title, String message) {
        // Runs on the main UI thread, instead of a background thread
        // and shows the alert
        runOnUiThread(() -> Toast.makeText(this,
                title + ": " + message, Toast.LENGTH_LONG).show());
    }
}
