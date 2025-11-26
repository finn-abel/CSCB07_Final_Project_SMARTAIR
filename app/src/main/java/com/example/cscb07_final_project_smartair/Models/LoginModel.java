package com.example.cscb07_final_project_smartair.Models;

import androidx.annotation.NonNull;

import com.example.cscb07_final_project_smartair.Presenters.LoginPresenter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginModel {

    private FirebaseAuth mAuth;

    public interface OnResetPasswordFinishedListener {
        void onResetPasswordSuccess(String message);

        void onResetPasswordFailure(String errorMessage);
    }

    public LoginModel() {
        mAuth = FirebaseAuth.getInstance();
    }

    public interface OnLoginFinishedListener {
        void onLoginSuccess();
        void onLoginFailure(String errorMessage);
    }



    public void signInUser(String email, String password, OnLoginFinishedListener listener) {
        // Credential Validation by Liam

        if (email.isEmpty()){
            listener.onLoginFailure("Email cannot be empty.");
            return;
        }
        if (password.isEmpty()){
            listener.onLoginFailure("Password cannot be empty.");
            return;
        }

        //Assuming Credentials are valid
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

    public void sendPasswordResetEmail(String email, OnResetPasswordFinishedListener listener) {

        if (email.isEmpty()) {
            listener.onResetPasswordFailure("Email cannot be empty.");
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            listener.onResetPasswordSuccess("Password reset email sent. Please check your email.");
                        } else {
                            String errorMessage = task.getException() != null ?
                                    task.getException().getMessage() : "Password reset failed.";
                            listener.onResetPasswordFailure(errorMessage);
                        }
                    }
                });
    }
}


