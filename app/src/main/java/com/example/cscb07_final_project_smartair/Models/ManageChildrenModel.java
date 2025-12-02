package com.example.cscb07_final_project_smartair.Models;

import androidx.annotation.NonNull;

import com.example.cscb07_final_project_smartair.Presenters.ManageChildrenPresenter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.example.cscb07_final_project_smartair.Users.ChildPermissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageChildrenModel extends BaseModel{
    private final ManageChildrenPresenter presenter;

    public ManageChildrenModel(ManageChildrenPresenter presenter) {
        this.presenter = presenter;
    }

    public void getProviders(String childId) {
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();

        root.child("users").child("children").child(childId).child("sharingPerms")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {


                        //return empty list on empty snapshot
                        if (!snapshot.exists()) {
                            presenter.onProvidersLoaded(new ArrayList<>());
                            return;
                        }

                        //convert format to map
                        GenericTypeIndicator<HashMap<String, ChildPermissions>> t =
                                new GenericTypeIndicator<HashMap<String, ChildPermissions>>() {};

                        Map<String, ChildPermissions> permMap = snapshot.getValue(t); //get db data
                        List<ChildPermissions> fullList = new ArrayList<>();

                        // convert into list
                        if (permMap != null) { //loop through entries to populate list
                            for (Map.Entry<String, ChildPermissions> entry : permMap.entrySet()) {
                                fullList.add(entry.getValue());
                            }
                        }
                        //send to presenter
                        presenter.onProvidersLoaded(fullList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        presenter.onFailure(error.getMessage());
                    }
                });
    }

    public void setPerms(String childId, Map<String, ChildPermissions> permMap) {
        DatabaseReference path = FirebaseDatabase.getInstance()
                .getReference("users")
                .child("children")
                .child(childId)
                .child("sharingPerms");

        path.setValue(permMap) //update data
                .addOnFailureListener(e -> {
                    if (presenter != null) presenter.onFailure(e.getMessage());
                });
    }


    //////PEF GUIDANCE SETUP///////
    public interface GuidanceListener {
        void onGuidanceLoaded(String red, String yellow, String green, float pb_pef);
    }

    public void getPefDetails(String childId, GuidanceListener listener) {
        DatabaseReference path = FirebaseDatabase.getInstance()
                .getReference("users/children")
                .child(childId);

        path.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    listener.onGuidanceLoaded("", "", "", 0);
                    return; // in case non existent
                }

                // get pb
                Float pb = snapshot.child("pb_pef").getValue(Float.class);
                float pbValue = (pb != null) ? pb : 0;

                // get guidance strings
                DataSnapshot guideSnap = snapshot.child("pef_guidance");
                String red = guideSnap.child("red").getValue(String.class);
                String yellow = guideSnap.child("yellow").getValue(String.class);
                String green = guideSnap.child("green").getValue(String.class);

                // handle nulls
                if (red == null) { red = ""; }
                if (yellow == null) { yellow = ""; }
                if (green == null) { green = ""; }

                listener.onGuidanceLoaded(red, yellow, green, pbValue);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void savePefDetails(String childId, String red, String yellow, String green, float pb) {
        DatabaseReference path = FirebaseDatabase.getInstance().getReference("users/children").child(childId);

        //write to hashmap
        Map<String, Object> updates = new HashMap<>();
        updates.put("pef_guidance/red", red);
        updates.put("pef_guidance/yellow", yellow);
        updates.put("pef_guidance/green", green);
        updates.put("pb_pef", pb);

        //add hashmap to db
        path.updateChildren(updates).addOnSuccessListener(aVoid -> presenter.onSuccess("Saved!"));
    }
}
