package com.example.cscb07_final_project_smartair.Views;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.cscb07_final_project_smartair.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class BaseParentActivity extends BaseActivity {

    public String parentId;
    public String activeChildId = null;
    public DatabaseReference mdatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mdatabase = FirebaseDatabase.getInstance().getReference();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            parentId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else { //no user logged in (edge case)
            finish();
            return;
        }

        if (activeChildId != null) {
            allAlerts(activeChildId);
        }
    }

    // Below used to avoid duplicate alerts
    private final HashMap<String, Long> lastAlertMap = new HashMap<>();

    private long getLast(String key) {
        return lastAlertMap.getOrDefault(key, 0L);
    }

    private void setLast(String key, long value) {
        lastAlertMap.put(key, value);
    }

    private void listenForChildrenAlerts() {
        DatabaseReference ref = mdatabase
                .child("users")
                .child("parents")
                .child(parentId);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot receiver) {
                for (DataSnapshot child : receiver.getChildren()) {
                    String childId = child.getKey();
                    allAlerts(childId);
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public void allAlerts(String childId) {
        listenTodayZone(childId);
        listenRapidRescue(childId);
        listenWorseAfterDose(childId);
        listenTriage(childId);
        listenInventory(childId);
    }

//    // Listens to alerts for each child
//    private void listenForChildrenAlerts() {
//
//        ValueEventListener childListener = new ValueEventListener() {
//
//            // Iterates through all children and runs whenever child/data is added,
//            // removed or updated
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot childSnap : dataSnapshot.getChildren()) {
//
//                    // Gets current child's ID so activeChildId is never null
//                    String childId = childSnap.getKey();
//                    if (childSnap.hasChild(parentId)) {
//
//                        if (activeChildId == null) {
//                            activeChildId = childId;
//                        }
//
//                        // Listeners for each alert category
//                        listenTriage(childId);
//                        listenTodayZone(childId);
//                        listenRapidRescue(childId);
//                        listenInventory(childId);
//                    }
//                }
//            }
//
//            // Handles read errors
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.e("ParentListener", "Failed to read children",
//                        databaseError.toException());
//            }
//        };
//
//        // Listens to the data in the 'parentsByChild' node
//        DatabaseReference childrenRef = mdatabase.child("parentsByChild");
//        childrenRef.addValueEventListener(childListener);
//    }

    // Today zone listener, alert for the child's zone today
    // Listens to the child based on the ID passed as an argument
    private void listenTodayZone(String childId) {
        DatabaseReference zoneRef =
                mdatabase.child("pef").child(childId);

        zoneRef.addValueEventListener(new ValueEventListener() {
            // Runs whenever child id's pef data changes
            @Override
            public void onDataChange(@NonNull DataSnapshot receiver) {
                double newestRatio = -1;
                long newestTimestamp = 0;

                // Gets newest timestamp to ensure that a previous pef entry
                // is not considered
                for (DataSnapshot child : receiver.getChildren()) {
                    Double current = child.child("current").getValue(Double.class);
                    Double pb = child.child("pb").getValue(Double.class);
                    Long timestamp = child.child("timestamp").getValue(Long.class);

                    if (current == null || pb == null || timestamp == null) continue;
                    // Skip if any of the above are missing

                    double ratio = current / pb;

                    if (timestamp > newestTimestamp) {
                        newestTimestamp = timestamp;
                        newestRatio = ratio;
                    }
                }

                if (newestRatio < 0.5 && newestTimestamp > getLast("redzone_" + childId)) {
                    showAlert("Red Zone", "Your child is in the red asthma zone.");
                    setLast("redzone_" + childId, newestTimestamp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // Rapid rescue repeats listener, listens for rescue uses
    private void listenRapidRescue(String childId) {
        DatabaseReference logsRef = mdatabase
                .child("medicine")
                .child("rescue")
                .child(childId);

        logsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot receiver) {
                long now = System.currentTimeMillis();
                int count = 0;
                long newestTimestamp = 0;

                for (DataSnapshot child : receiver.getChildren()) {
                    Long timestamp = child.child("timestamp").getValue(Long.class);

                    // check for last 3 hours
                    if (timestamp != null && timestamp >= now - (3 * 60 * 60 * 1000)) {
                        count++;
                        newestTimestamp = Math.max(newestTimestamp, timestamp);
                    }
                }

                // make sure not checking duplicates
                if (count >= 3 && newestTimestamp > getLast("rapid_" + childId)) {
                    showAlert("Rescue Alert", "Used rescue inhaler 3 times in 3 hours.");
                    setLast("rapid_" + childId, newestTimestamp);
                }
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // Worse after dose listener, checks and alerts if the child is feeling worse after a dose
    private void listenWorseAfterDose(String childId) {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot receiver) {
                long newestTimestamp = 0;
                boolean worse = false;

                for (DataSnapshot child : receiver.getChildren()) {
                    Double before = child.child("breathingBefore").getValue(Double.class);
                    Double after = child.child("breathingAfter").getValue(Double.class);
                    Long timestamp = child.child("timestamp").getValue(Long.class);

                    if (before == null || after == null || timestamp == null) continue;
                    // skip if missing

                    if (after < before && timestamp > newestTimestamp) {
                        newestTimestamp = timestamp;
                        worse = true; // after rating < before rating
                    }
                }

                if (worse && newestTimestamp > getLast("worse_" + childId)) {
                    showAlert("Dose Issue", "Your child felt worse after their dose.");
                    setLast("worse_" + childId, newestTimestamp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };

        // Listener for both controller and rescue logs
        mdatabase.child("medicine").child("controller").child(childId)
                .addValueEventListener(listener);
        mdatabase.child("medicine").child("rescue").child(childId)
                .addValueEventListener(listener);
    }

    // Triage escalation listener
    private void listenTriage(String childId) {
        DatabaseReference triageRef = mdatabase.child("triage_incidents").child(childId);

        triageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot receiver) {
                long newestTimestamp = 0;
                boolean escalated = false;

                for (DataSnapshot child : receiver.getChildren()) {
                    Boolean temp = child.child("escalation").getValue(Boolean.class);
                    Long timestamp = child.child("timestamp").getValue(Long.class);

                    if (temp != null && temp && timestamp != null && timestamp > newestTimestamp) {
                        newestTimestamp = timestamp;
                        escalated = true;
                    }
                }

                if (escalated && newestTimestamp > getLast("triage_" + childId)) {
                    showAlert("Triage Escalation", "Your child's triage has escalated.");
                    setLast("triage_" + childId, newestTimestamp);
                }
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // Inventory low/expired listener, alerts the parent if the child's medication is low
    // or expired
    private void listenInventory(String childId) {
        DatabaseReference inventoryRef = mdatabase.child("inventory").child(childId);

        inventoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot receiver) {
                long newestTimestamp = System.currentTimeMillis();

                for (DataSnapshot child : receiver.getChildren()) {
                    Boolean expired = child.child("expired").getValue(Boolean.class);
                    Boolean low = child.child("low").getValue(Boolean.class);

                    if (Boolean.TRUE.equals(expired)
                            && newestTimestamp > getLast("inventoryexpired_" + childId)) {
                        showAlert("Expired Medication", "Your child's medication expired.");
                        setLast("inventoryexpired_" + childId, newestTimestamp);
                    }

                    if (Boolean.TRUE.equals(low)
                            && newestTimestamp > getLast("inventorylow_" + childId)) {
                        showAlert("Low Medication", "Your child's medication is low.");
                        setLast("inventorylow_" + childId, newestTimestamp);
                    }
                }
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // Displays a popup method for all the alerts on the parent's app
    private void showAlert(String title, String message) {
        // Runs on the main UI thread, instead of a background thread
        // and shows the alert
        runOnUiThread(() -> Toast.makeText(this,
                title + ": " + message, Toast.LENGTH_LONG).show());
    }
}
