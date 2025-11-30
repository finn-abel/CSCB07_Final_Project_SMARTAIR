package com.example.cscb07_final_project_smartair.Models;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class ChildSignUpModel extends SignUpModel {


    public ChildSignUpModel() {
        super();
    }

    public int passwordcheck(String pw){     //check password strength
        return super.passwordcheck(pw);
    }

    @Override
    public void createUser(String email, String password, OnSignUpFinishedListener listener) {

        if (email.isEmpty()){
            listener.onSignUpFailure("Username cannot be empty");
            return;
        }

        if (password.isEmpty()){
            listener.onSignUpFailure("Password cannot be empty");
            return;
        }

        //Assuming valid credentials
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // The user was created successfully.
                            listener.onSignUpSuccess();
                        } else {
                            // If sign up fails, display a message to the user.
                            String errorMessage = task.getException() != null ?
                                    task.getException().getMessage() : "Authentication failed.";
                            listener.onSignUpFailure(errorMessage);
                        }
                    }
                });
    }
}

