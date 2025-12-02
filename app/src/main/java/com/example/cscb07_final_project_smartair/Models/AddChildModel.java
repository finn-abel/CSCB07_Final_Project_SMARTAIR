package com.example.cscb07_final_project_smartair.Models;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import com.example.cscb07_final_project_smartair.Users.Child;
import com.example.cscb07_final_project_smartair.Presenters.AddChildPresenter;
import com.example.cscb07_final_project_smartair.Users.pefGuidance;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AddChildModel {
    private final AddChildPresenter presenter;
    private final FirebaseAuth mAuth;
    public AddChildModel(AddChildPresenter presenter) {
        this.presenter = presenter;
        mAuth = FirebaseAuth.getInstance();
    }
    public void createUser(String name, String email, String password, long dob, String notes, Float pef) {
        String parent = mAuth.getCurrentUser().getUid();

        FirebaseOptions options = FirebaseApp.getInstance().getOptions(); //get current instance data
        String tempAppName = "ChildCreator";
        FirebaseApp secondaryApp;
        try {
            secondaryApp = FirebaseApp.getInstance(tempAppName);
        } catch (IllegalStateException e) {
            secondaryApp = FirebaseApp.initializeApp(FirebaseApp.getInstance().getApplicationContext(), options, tempAppName);
        }
        FirebaseAuth secondaryAuth = FirebaseAuth.getInstance(secondaryApp); //gets temp instance

        secondaryAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = secondaryAuth.getCurrentUser();
                            String uid = user.getUid();
                            Child child = new Child(name, uid, email, parent, new pefGuidance(),
                                    dob,notes,pef);

                            HashMap<String, Object> updates = new HashMap<>();
                            updates.put("users/children/" + uid, child);
                            updates.put("users/parents/" + parent + "/children/" + uid + "/name", name);
                            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                            rootRef.updateChildren(updates)
                                    .addOnSuccessListener(aVoid -> presenter.onSignUpSuccess())
                                    .addOnFailureListener(e -> presenter.onSignUpFailure(e.getMessage()));
                        } else {
                            String errorMessage = task.getException() != null ?
                                    task.getException().getMessage() : "Authentication failed.";
                            presenter.onSignUpFailure(errorMessage);
                        }
                    }
                });
    }
}
