package com.example.cscb07_final_project_smartair.Views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.cscb07_final_project_smartair.Presenters.TriagePresenter;
import com.example.cscb07_final_project_smartair.R;

/*
This class acts as a superclass for all activities to be used in this app. It allows for
implementation of additional functions shared between all activities
 */
public abstract class BaseActivity extends AppCompatActivity implements TriageView{

    private TriagePresenter Tpresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        createNotificationChannel();//setup channel for recheck
        Tpresenter = new TriagePresenter(this, this); //set presenter
    }

    protected void callTriage(boolean recheck){
        Tpresenter.callTriage(recheck);
    }

    @Override
    public void showCheckup(){
        new CheckupFragment().show(getSupportFragmentManager(),"CHECKUP");
    }

    @Override
    public void showDecision(boolean isRedFlag){
        DecisionFragment decision = new DecisionFragment();
        decision.isRedFlag=isRedFlag;
        decision.show(getSupportFragmentManager(),"DECISION");
    }

    @Override
    public void showSteps(){
        //not implemented yet
    }

    @Override
    public void closeDialog(){
        Fragment previous = getSupportFragmentManager().findFragmentByTag("CHECKUP");
        if(previous != null){
            ((DialogFragment)previous).dismiss();
        }//close dialog
    }

    @Override
    public void onFormSubmit(boolean speak, boolean lips, boolean chest){
        Tpresenter.submitForm(lips,speak,chest);
    }//submit form

    @Override
    public void setContentView(int layoutResID){
        super.setContentView(layoutResID);

        android.view.View triageButton = findViewById(R.id.triage_button);
        if(triageButton!=null){ //if implemented
            triageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Tpresenter.callTriage(false);
                }
            });
        }
    }

    public void showTimerStart(){
        closeDialog();
        Tpresenter.startTimer();
    }

    public void callEmergency(){
        closeDialog();
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:16476144487")); //have 911 ready to dial
        startActivity(intent);
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            android.app.NotificationChannel channel = new android.app.NotificationChannel(
                    "TRIAGE_RECHECK_CHANNEL",
                    "Breathing issue checkups",
                    android.app.NotificationManager.IMPORTANCE_HIGH
            );

            android.app.NotificationManager notifManager = getSystemService(android.app.NotificationManager.class);
            if (notifManager != null) {
                notifManager.createNotificationChannel(channel);
            }
        }
    }

}

