package com.example.cscb07_final_project_smartair.Views;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.Toast;

import com.example.cscb07_final_project_smartair.Presenters.SignUpPresenter;
import com.example.cscb07_final_project_smartair.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;


public class SignUpActivity extends BaseActivity implements SignUpView {

    private TextInputEditText new_user_email;
    private TextInputEditText new_user_password;


    private SignUpPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        new_user_email = findViewById(R.id.new_user_email);
        new_user_password = findViewById(R.id.new_user_password);
        Button sign_up_button = findViewById(R.id.sign_up_button);
        Button sign_in_button = findViewById(R.id.SIB);



        presenter = new SignUpPresenter(this);

        sign_up_button.setOnClickListener(view -> {
            presenter.onSignUpButtonClicked();
        });

        sign_in_button.setOnClickListener(view -> {
            presenter.onSignInButtonClicked();
        });



        new_user_password.addTextChangedListener(new TextWatcher() { //monitor changes to password
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
        return Objects.requireNonNull(new_user_email.getText()).toString().trim();
    }

    @Override
    public String getPassword() {
        return Objects.requireNonNull(new_user_password.getText()).toString();
    }

    @Override
    public void enableSignUp(boolean status){
        Button sign_up_button=findViewById(R.id.sign_up_button);
        sign_up_button.setEnabled(status); // enable/disable button
        sign_up_button.setAlpha(status ? 1.0f : 0.5f); //change transparency based on status
    }

    @Override
    public void showPasswordError(String message){
        new_user_password.setError(message); //display error message
    }

    @Override
    public void clearPasswordError(){
        new_user_password.setError(null); //clear error message
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


