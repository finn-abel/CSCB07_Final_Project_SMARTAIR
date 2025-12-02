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
import com.google.firebase.database.FirebaseDatabase;

public class AddChildModel {
    private final AddChildPresenter presenter;
    private final FirebaseAuth mAuth;
    public AddChildModel(AddChildPresenter presenter) {
        this.presenter = presenter;
        mAuth = FirebaseAuth.getInstance();
    }
    public void createUser(String name, String email, String password, long dob, String notes) {
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
                            Child child = new Child(name, uid, email, parent, null);
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users")
                                    .child("children")
                                    .child(uid);
                            ref.setValue(child)
                                    .addOnSuccessListener(aVoid -> {
                                        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("users")
                                                .child("parents")
                                                .child(parent)
                                                .child("children")
                                                .child(uid)
                                                .child("name");
                                        ref2.setValue(name)
                                                .addOnSuccessListener(aVoid2 -> presenter.onSignUpSuccess())
                                                .addOnFailureListener(e -> presenter.onSignUpFailure(e.getMessage()));
                                    })
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
