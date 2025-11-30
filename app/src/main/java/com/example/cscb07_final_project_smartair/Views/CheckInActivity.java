package com.example.cscb07_final_project_smartair.Views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cscb07_final_project_smartair.Presenters.CheckInPresenter;
import com.example.cscb07_final_project_smartair.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class CheckInActivity extends BaseActivity implements CheckInView{

    private EditText symptoms;
    private EditText other_triggers;
    private EditText date;

    private CheckBox dust;
    private CheckBox smoke;
    private CheckBox pets;
    private CheckBox cold_air;
    private CheckBox odour;
    private CheckBox other;

    private CheckInPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
        symptoms = findViewById(R.id.symptoms);
        other_triggers = findViewById(R.id.other_text);
        date = findViewById(R.id.date);
        dust = findViewById(R.id.dust);
        smoke = findViewById(R.id.smoke);
        pets = findViewById(R.id.pets);
        cold_air = findViewById(R.id.cold_air);
        odour = findViewById(R.id.odour);
        other = findViewById(R.id.other);
        Button submit = findViewById(R.id.submit_triage);

        presenter = new CheckInPresenter(this);

        submit.setOnClickListener(view -> {
            presenter.onSubmitButtonClicked();
        });


    }

    @Override
    public String getDate(){
        return Objects.requireNonNull(date.getText()).toString();
    }


    @Override
    public ArrayList<String> getTriggers(){
        ArrayList<String> triggers = new ArrayList<>();
        if(dust.isChecked()){
            triggers.add(dust.getText().toString());
        }
        if(smoke.isChecked()){
            triggers.add(smoke.getText().toString());
        }
        if(pets.isChecked()){
            triggers.add(pets.getText().toString());
        }
        if(cold_air.isChecked()){
            triggers.add(cold_air.getText().toString());
        }
        if(odour.isChecked()){
            triggers.add(odour.getText().toString());
        }

        if(other.isChecked()) {
            ArrayList<String> other_triggers = getOtherTriggers();
            for (String trigger : other_triggers) {
                if (!triggers.contains(trigger)) {
                    triggers.add(trigger);
                }
            }
        }

        return triggers;
    }

    @Override
    public ArrayList<String> getOtherTriggers(){
        String[] split_triggers = Objects.requireNonNull(other_triggers.getText()).toString().split(",");
        return new ArrayList<>(Arrays.asList(split_triggers));
    }

    @Override
    public ArrayList<String> getSymptoms(){
        String[] split_symptoms = Objects.requireNonNull(symptoms.getText()).toString().split(",");
        return new ArrayList<>(Arrays.asList(split_symptoms));
    }

    @Override
    public void showCheckInSuccess(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showCheckInFailure(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToMainScreen() {
        startActivity(new Intent(this, MainActivityView.class));
        finish();
    }


}
