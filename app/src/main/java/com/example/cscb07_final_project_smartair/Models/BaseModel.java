package com.example.cscb07_final_project_smartair.Models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.cscb07_final_project_smartair.Users.ChildSpinnerOption;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BaseModel

{

    public BaseModel(){
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
                    public void onDataChange(DataSnapshot snapshot) {
                        List<ChildSpinnerOption> childrenList = new ArrayList<>();

                        if (snapshot.exists()) {
                            for (DataSnapshot childSnap : snapshot.getChildren()) {
                                String childId = childSnap.getKey();
                                String name = childSnap.child("name").getValue(String.class);

                                Log.d("DEBUG", "Found childId=" + childId + " name=" + name);

                                if (name == null) {name = "Unknown";}
                                childrenList.add(new ChildSpinnerOption(childId, name));
                            }
                        }
                        listener.onChildrenLoaded(childrenList);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        listener.onError(error.getMessage());
                    }
                });
    }

    // set up provider spinner
    public class ProviderSpinnerOption {
        public String providerId;
        public String name;

        public ProviderSpinnerOption(String providerId, String name) {
            this.providerId = providerId;
            this.name = name;
        }

        @Override
        public String toString() {
            return name; // name of provider for the spinner
        }
    }

    public interface ProviderFetchListener {
        void onProvidersLoaded(List<ProviderSpinnerOption> providers);
        void onError(String message);
    }

    public void fetchProvidersForChild(String childId, ProviderFetchListener listener) {

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child("children")
                .child(childId)
                .child("sharingPerms");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot receiver) {

                List<ProviderSpinnerOption> providerList = new ArrayList<>();

                for (DataSnapshot child : receiver.getChildren()) {

                    String providerId = child.getKey();
                    if (providerId == null) continue; // no provider id

                    Map<String, Object> providerData = (Map<String, Object>) child.getValue();
                    if (providerData != null) {
                        String name = (String) providerData.get("name"); // get the provider's name
                        if (name != null) {
                            // Add the provider to the list
                            providerList.add(new ProviderSpinnerOption(providerId, name));
                        }
                    }
                }

                listener.onProvidersLoaded(providerList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onError(error.getMessage());
            }
        });
    }
}
