package com.example.cscb07_final_project_smartair.Models;

import com.example.cscb07_final_project_smartair.Users.ChildSpinnerOption;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

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
}
