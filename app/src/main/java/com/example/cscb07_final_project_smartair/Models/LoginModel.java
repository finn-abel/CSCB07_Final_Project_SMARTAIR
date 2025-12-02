package com.example.cscb07_final_project_smartair.Models;

import androidx.annotation.NonNull;

import com.example.cscb07_final_project_smartair.Presenters.LoginPresenter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginModel {

    protected FirebaseAuth mAuth;

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



    //Carries out Firebase authentication based on user input to sign in to the app
    public void signInUser(String email, String password, OnLoginFinishedListener listener) {

        if (email.isEmpty()){
            listener.onLoginFailure("Email cannot be empty.");
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

    //Sends a password reset email to the user's email address
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
                            listener.onResetPasswordFailure("Password reset failed.");
                        }
                    }
                });
    }
}


