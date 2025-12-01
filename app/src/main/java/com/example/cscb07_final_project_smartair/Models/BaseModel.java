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
}
