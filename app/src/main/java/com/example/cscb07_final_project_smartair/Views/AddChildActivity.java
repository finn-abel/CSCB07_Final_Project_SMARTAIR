package com.example.cscb07_final_project_smartair.Views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cscb07_final_project_smartair.R;
import com.example.cscb07_final_project_smartair.Presenters.AddChildPresenter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

public class AddChildActivity extends AppCompatActivity implements AddChildView {
    private EditText name, username, password, dob, notes;
    private Button add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_child);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        name = findViewById(R.id.name);
        username = findViewById(R.id.child_username);
        password = findViewById(R.id.child_password);
        dob = findViewById(R.id.dob);
        notes = findViewById(R.id.notes);
        add = findViewById(R.id.add_child);

        AddChildPresenter presenter = new AddChildPresenter(this);
        add.setOnClickListener(view -> presenter.onAddChildButtonClicked());
    }

    @Override
    public String getName() {
        return Objects.requireNonNull(name.getText()).toString().trim();
    }

    @Override
    public String getEmail() {
        return Objects.requireNonNull(username.getText()).toString().trim()+"@smartair.com";
    }

    @Override
    public String getPassword() {
        return Objects.requireNonNull(password.getText()).toString();
    }

    @Override
    public String getDob() {
        return Objects.requireNonNull(dob.getText()).toString();
    }

    @Override
    public String getNotes() {
        return Objects.requireNonNull(notes.getText()).toString();
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
    public void navigateToManageScreen() {
        startActivity(new Intent(this, ManageChildrenActivity.class));
        finish();
    }
}