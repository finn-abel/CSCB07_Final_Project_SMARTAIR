package com.example.cscb07_final_project_smartair.Presenters;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.example.cscb07_final_project_smartair.Models.TriageModel;
import com.example.cscb07_final_project_smartair.Views.TriageView;
import com.example.cscb07_final_project_smartair.Receivers.TriageReceiver;

public class TriagePresenter {
    private TriageView Tview;
    private Context context;
    public boolean recheck;

    public TriagePresenter(TriageView view, Context context){
        this.Tview = view;
        this.context = context;
        this.recheck=false; //set to false by default
    }//constructor

    public void callTriage(boolean recheck){
        this.recheck=recheck;
        Tview.showCheckup();
    }//call triage from button

    public void submitForm(boolean lips, boolean speak, boolean chest){
        TriageModel Tmodel=new TriageModel("USER_ID", speak, lips, chest);
        if(recheck){
            recheck=false;
            Tmodel.logIncident();
            if(Tmodel.isRedFlag()){
                Tview.callEmergency();
                return;
            }
        }
        Tmodel.logIncident();
        Tview.closeDialog();//close current dialog view
        Tview.showDecision(Tmodel.isRedFlag());
    }
    public void startTimer(){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, TriageReceiver.class);

        PendingIntent pendingInt = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        ); //create request to create receiver

        long triggerTime = System.currentTimeMillis() + ( 10 * 1000);

        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingInt); //set request to open in time
    }
}





