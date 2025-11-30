package com.example.cscb07_final_project_smartair.Views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cscb07_final_project_smartair.Presenters.ChildLoginPresenter;
import com.example.cscb07_final_project_smartair.Presenters.LoginPresenter;
import com.example.cscb07_final_project_smartair.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;


public class ChildLoginActivity extends BaseActivity implements ChildLoginView {

    private TextInputEditText username;
    private TextInputEditText user_password;

    private ChildLoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_login);


        username = findViewById(R.id.username);
        user_password = findViewById(R.id.user_password);
        Button sign_in_button = findViewById(R.id.sib);
        Button sign_up_button = findViewById(R.id.sub);


        presenter = new ChildLoginPresenter(this);


        sign_in_button.setOnClickListener(view -> {
            presenter.onLoginButtonClicked();
        });

        sign_up_button.setOnClickListener(view -> {
            presenter.onSignUpButtonClicked();
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
        return Objects.requireNonNull(username.getText()).toString().trim()+"@smartair.com";
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
        startActivity(new Intent(this, MainActivityView.class));
        finish();
    }

    @Override
    public void navigateToSignUpScreen() {

        startActivity(new Intent(this, ChildSignUpActivity.class));
    }

}
