package com.example.cscb07_final_project_smartair.Views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.cscb07_final_project_smartair.Presenters.LoginPresenter;
import com.example.cscb07_final_project_smartair.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;


public class LoginActivity extends BaseActivity implements LoginView {

    private TextInputEditText user_email;
    private TextInputEditText user_password;

    private LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        user_email = findViewById(R.id.user_email);
        user_password = findViewById(R.id.user_password);
        Button sign_in_button = findViewById(R.id.sib);
        Button sign_up_button = findViewById(R.id.sub);
        Button forgot_password_button = findViewById(R.id.fpb);


        presenter = new LoginPresenter(this);


        sign_in_button.setOnClickListener(view -> {
            presenter.onLoginButtonClicked();
        });

        sign_up_button.setOnClickListener(view -> {
            presenter.onSignUpButtonClicked();
        });

        forgot_password_button.setOnClickListener(view -> {
            presenter.onForgotPasswordButtonClicked();
        });
    }

    @Override
    public void showPasswordResetSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showPasswordResetFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public String getEmail() {
        return Objects.requireNonNull(user_email.getText()).toString().trim();
    }

    @Override
    public String getPassword() {
        return Objects.requireNonNull(user_password.getText()).toString();
    }

    @Override
    public void showLoginSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoginFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showValidationError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToMainScreen() {

        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String role = prefs.getString("USER_ROLE", "");
        //pull role

        if(role.equals("PARENT")) {
            startActivity(new Intent(this, ParentHomeActivity.class));

        } else if (role.equals("CHILD")) {
            startActivity(new Intent(this, MainActivityView.class));
        }
        finish();
    }

    @Override
    public void navigateToSignUpScreen() {

        startActivity(new Intent(this, SignUpActivity.class));
    }

}

