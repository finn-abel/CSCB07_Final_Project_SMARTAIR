package com.example.cscb07_final_project_smartair.Views;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cscb07_final_project_smartair.Presenters.CheckInPresenter;
import com.example.cscb07_final_project_smartair.Presenters.PEFPresenter;
import com.example.cscb07_final_project_smartair.R;

public class PEFActivity extends BaseActivity implements PEFView{

    PEFPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pef);

        this.presenter = new PEFPresenter(this);

        Button enter = findViewById(R.id.enter_pef);
        Button return_pef = findViewById(R.id.return_pef);

        enter.setOnClickListener(view -> {
            presenter.onEnterClicked();
        });

        return_pef.setOnClickListener(view -> {
            this.navigateToMainActivity();
        });

    }

    @Override
    public void navigateToMainActivity(){
        startActivity(new Intent(this, MainActivityView.class)); //go to main screen
        finish();
    }

    @Override
    public void showPEFError(String msg){
        EditText current_pef = findViewById(R.id.current_pef);
        current_pef.setError(msg);
    }

    @Override
    public void showSuccess(){
        Toast.makeText(this, "Saved successfully!", Toast.LENGTH_SHORT).show();
        navigateToMainActivity();
    }

    @Override
    public void showFailure(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public String getCurrent(){
        EditText current_pef = findViewById(R.id.current_pef);
        return current_pef.getText().toString().trim();
    }
    @Override
    public String getPre(){
        EditText pre_med_pef = findViewById(R.id.pre_med_pef);
        return pre_med_pef.getText().toString().trim();
    }

    @Override
    public String getPost(){
        EditText post_med_pef = findViewById(R.id.post_med_pef);
        return post_med_pef.getText().toString().trim();
    }
}


