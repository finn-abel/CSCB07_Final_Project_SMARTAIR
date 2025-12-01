package com.example.cscb07_final_project_smartair.Models;

import androidx.annotation.NonNull;

import com.example.cscb07_final_project_smartair.Presenters.LoginPresenter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class ChildLoginModel extends LoginModel {


    public ChildLoginModel() {
        super();
    }




    @Override
    public void signInUser(String email, String password, OnLoginFinishedListener listener) {


        if (email.isEmpty()){
            listener.onLoginFailure("Username cannot be empty.");
            return;
        }
        if (password.isEmpty()){
            listener.onLoginFailure("Password cannot be empty.");
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            listener.onLoginSuccess();
                        } else {
                            String errorMessage = task.getException() != null ?
                                    task.getException().getMessage() : "Authentication failed.";
                            listener.onLoginFailure(errorMessage);
                        }
                    }
                });
    }

}



