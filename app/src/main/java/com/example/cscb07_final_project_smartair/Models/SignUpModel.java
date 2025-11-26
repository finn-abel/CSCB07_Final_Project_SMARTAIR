package com.example.cscb07_final_project_smartair.Models;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpModel {

    private final FirebaseAuth mAuth;

    public SignUpModel() {
        this.mAuth = FirebaseAuth.getInstance();
    }

    public interface OnSignUpFinishedListener {
        void onSignUpSuccess();
        void onSignUpFailure(String errorMessage);
    }

    public int passwordcheck(String pw){     //check password strength
        if((pw==null)||(pw.length()<7))             //no password or too short
            return -2; //password too short
        int caps =0, lwr=0, num=0, sym=0, inv=0;
        String symbols = "!@#$%&";                  //valid symbols
        for(int x=0;x<pw.length();x++){             //iterate through string
            char c=pw.charAt(x);
            if(Character.isUpperCase(c))            //check uppercase
                caps++;
            else if(Character.isLowerCase(c))       //check lowercase
                lwr++;
            else if(Character.isDigit(c))           //check numbers
                num++;
            else if(symbols.indexOf(c)!=-1)         //check for symbols
                sym++;
            else
                inv++;                              //invalid character found
        }

        if( (pw.length()>=8) && (caps>=1) && (lwr>=1) && (num>=1) && (sym>=1) && (inv==0) )
            return 1; //valid password
        return -1;  // invalid password
    }

    public void createUser(String email, String password, OnSignUpFinishedListener listener) {

        if (email.isEmpty()){
            listener.onSignUpFailure("Email cannot be empty");
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


