package com.example.cscb07_final_project_smartair.Views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cscb07_final_project_smartair.Presenters.SignUpPresenter;
import com.example.cscb07_final_project_smartair.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;


public class SignUpActivity extends AppCompatActivity implements SignUpView {

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
        //Button child_account_button = findViewById(R.id.child_account_button);



        presenter = new SignUpPresenter(this);

        sign_up_button.setOnClickListener(view -> {
            presenter.onSignUpButtonClicked();
        });

        sign_in_button.setOnClickListener(view -> {
            presenter.onSignInButtonClicked();
        });

        /*child_account_button.setOnClickListener(view -> {
            presenter.onChildAccountButtonClicked();
        }); -- to be implemented later*/

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

    /* To be implemented later
    @Override
    public void navigateToChildSignUpScreen() {
        Intent intent = new Intent(this, ChildSignUpActivity.class);
        startActivity(intent);
    } */

}


