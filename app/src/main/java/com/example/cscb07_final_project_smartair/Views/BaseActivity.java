package com.example.cscb07_final_project_smartair.Views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.cscb07_final_project_smartair.DataObjects.triageCapture;
import com.example.cscb07_final_project_smartair.Presenters.TriagePresenter;
import com.example.cscb07_final_project_smartair.R;
import com.example.cscb07_final_project_smartair.Users.ChildSpinnerOption;

import java.util.List;

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

        if (getIntent().getBooleanExtra("SHOW_TRIAGE_RECHECK", false)) {
            String childID = getIntent().getStringExtra("CHILD_ID");
            getIntent().removeExtra("SHOW_TRIAGE_RECHECK");
            Tpresenter.callTriage(true,childID);
        } //call on recheck
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent.getBooleanExtra("SHOW_TRIAGE_RECHECK", false)) {

            String childID = getIntent().getStringExtra("CHILD_ID");

            intent.removeExtra("SHOW_TRIAGE_RECHECK");
            if (Tpresenter != null) Tpresenter.callTriage(true,childID);
        }
    }// call recheck if app already open

    @Override
    public void callTriage(boolean recheck, String childID){
        Tpresenter.callTriage(recheck,childID);
    }

    @Override
    public void showCheckup(String childID){
        TriageFragment frag = new TriageFragment();
        if(childID!=null){
            frag.childID=childID;
        }
        frag.show(getSupportFragmentManager(),"TRIAGE_FRAGMENT");
    }

    @Override
    public void showDecision(boolean isRedFlag){
        Fragment triageDialog = getSupportFragmentManager().findFragmentByTag("TRIAGE_FRAGMENT");

        if (triageDialog instanceof TriageFragment && triageDialog.isVisible()) {
            ((TriageFragment) triageDialog).morphToDecision(isRedFlag); //change to decision mode
        } else { //on error, reopen
            TriageFragment newFrag = new TriageFragment();
            newFrag.show(getSupportFragmentManager(), "TRIAGE_FRAGMENT");
        }
    }

    @Override
    public void closeDialog(){
        Fragment previous = getSupportFragmentManager().findFragmentByTag("TRIAGE_FRAGMENT");
        if(previous != null){
            ((DialogFragment)previous).dismiss();
        }//close dialog
    }

    @Override
    public void onFormSubmit(triageCapture capture){
        Tpresenter.submitForm(capture);
    }//submit form

    @Override
    public void setContentView(int layoutResID){
        super.setContentView(layoutResID);

        android.view.View triageButton = findViewById(R.id.triage_button);
        if(triageButton!=null){ //if implemented
            triageButton.setOnClickListener( v -> {
                    Tpresenter.callTriage(false,null);
            });
        }
        android.view.View navButton = findViewById(R.id.btnTopNav);
        if (navButton != null) {
            navButton.setOnClickListener(v -> showNavMenu(navButton));
        }
    }
    private void showNavMenu(android.view.View anchor) {
        android.view.View menuView = getLayoutInflater().inflate(R.layout.view_nav_menu, null);

        PopupWindow popup = new PopupWindow(
                menuView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true
        );

        popup.setElevation(12f);

        menuView.findViewById(R.id.nav_checkin).setOnClickListener(v -> {
            startActivity(new Intent(this, CheckInActivity.class));
            popup.dismiss();
        });
        menuView.findViewById(R.id.nav_pef).setOnClickListener(v -> {
            startActivity(new Intent(this, PEFActivity.class));
            popup.dismiss();
        });
        menuView.findViewById(R.id.nav_meds).setOnClickListener(v -> {
            startActivity(new Intent(this, MedicineLogsActivity.class));
            popup.dismiss();
        });
        menuView.findViewById(R.id.nav_history).setOnClickListener(v -> {
            startActivity(new Intent(this, CheckInHistoryActivity.class));
            popup.dismiss();
        });
        menuView.findViewById(R.id.nav_home).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivityView.class));
            popup.dismiss();
        });

        popup.showAsDropDown(anchor, 0, 16);
    }


    @Override
    public void showTimerStart(triageCapture capture, long n){

        Tpresenter.getRemedy(capture);
        Tpresenter.logDecision("REMEDY", capture, false);
        Tpresenter.startTimer(n,capture.userID);
    }

    @Override
    public void showRemedy(String s, String level){
        Fragment triageDialog = getSupportFragmentManager().findFragmentByTag("TRIAGE_FRAGMENT");

        if (triageDialog instanceof TriageFragment && triageDialog.isVisible()) {
            ((TriageFragment) triageDialog).morphToRemedy(s,level); //change to decision mode
        } else { //on error, reopen
            TriageFragment newFrag = new TriageFragment();
            newFrag.show(getSupportFragmentManager(), "TRIAGE_FRAGMENT");
        }
    }

    @Override
    public void callEmergency(triageCapture capture, boolean escalation){
        Tpresenter.logDecision("EMERGENCY", capture, escalation);
        closeDialog();
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:16476144487")); //have 911 ready to dial
        startActivity(intent);
    }

    public void createNotificationChannel() {
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

    @Override
    public void showLogTriageSuccess(){
        Toast.makeText(this, "Saved incident successfully!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLogTriageFailure(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean isParent() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String role = prefs.getString("USER_ROLE", "");
        return role.equals("PARENT");
    }

    public Context getContext(){
        return this;
    }

    public void showError(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getChildren(){
        Tpresenter.pullChildren();
    }
    @Override
    public void passChildrenToFragment(List<ChildSpinnerOption> childrenList) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag("TRIAGE_FRAGMENT");

        if (currentFragment instanceof TriageFragment) {
            ((TriageFragment) currentFragment).updateSpinner(childrenList);
        }
    }

    @Override
    public void onChildSelected(String userID){
        Tpresenter.onChildSelected(userID);
    }

}

