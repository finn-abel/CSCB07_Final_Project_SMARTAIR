package com.example.cscb07_final_project_smartair.Views;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cscb07_final_project_smartair.Presenters.SignUpPresenter;
import com.example.cscb07_final_project_smartair.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;


public class SignUpActivity extends AppCompatActivity implements SignUpView {

    private TextInputEditText user_email_signup;
    private TextInputEditText user_password_signup;


    private SignUpPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        user_email_signup = findViewById(R.id.new_user_email);
        user_password_signup = findViewById(R.id.new_user_password);
        Button sign_up_button = findViewById(R.id.sign_up_button);
        Button sign_in_button = findViewById(R.id.SIB);


        presenter = new SignUpPresenter(this);

        sign_up_button.setOnClickListener(view -> {
            presenter.onSignUpButtonClicked();
        });

        sign_in_button.setOnClickListener(view -> {
            presenter.onSignInButtonClicked();
        });

        user_password_signup.addTextChangedListener(new TextWatcher() { //monitor changes to password
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                presenter.onPasswordEdit(s.toString()); //pass text to checker
            }
        });


    }


    @Override
    public String getEmail() {
        return Objects.requireNonNull(user_email_signup.getText()).toString().trim();
    }

    @Override
    public String getPassword() {
        return Objects.requireNonNull(user_password_signup.getText()).toString();
    }

    @Override
    public void enableSignUp(boolean status){
        Button sign_up_button=findViewById(R.id.sign_up_button);
        sign_up_button.setEnabled(status); // enable/disable button
        sign_up_button.setAlpha(status ? 1.0f : 0.5f); //change transparency based on status
    }

    @Override
    public void showPasswordError(String message){
        user_password_signup.setError(message); //display error message
    }

    @Override
    public void clearPasswordError(){
        user_password_signup.setError(null); //clear error message
    }

    @Override
    public void showSignUpSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSignUpFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    @Override
    public void navigateToLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}


