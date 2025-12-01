package com.example.cscb07_final_project_smartair.Presenters;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.example.cscb07_final_project_smartair.DataObjects.triageCapture;
import com.example.cscb07_final_project_smartair.Models.BaseModel;
import com.example.cscb07_final_project_smartair.Models.TriageModel;
import com.example.cscb07_final_project_smartair.Users.ChildSpinnerOption;
import com.example.cscb07_final_project_smartair.Views.TriageView;
import com.example.cscb07_final_project_smartair.Receivers.TriageReceiver;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class TriagePresenter implements BaseModel.ChildFetchListener{
    private TriageView Tview;
    private Context context;
    public boolean recheck;

    public String guidance;
    public TriageModel Tmodel;

    public TriagePresenter(TriageView view, Context context){
        this.Tview = view;
        this.context = context;
        this.recheck=false; //set to false by default
        this.Tmodel = new TriageModel(this);
    }//constructor

    public void callTriage(boolean recheck,String childID){
        this.recheck=recheck;
        Tview.showCheckup(childID);
    }//call triage from button

    public void submitForm(triageCapture capture){
        boolean redFlag = Tmodel.isRedFlag(capture);
        if (recheck) {
            Tmodel.isImprovement(capture, shouldEscalate -> { //check against previous log for escalation
                recheck = false; // reset flag
                if (shouldEscalate) {
                    this.guidance = "EMERGENCY";
                    Tview.callEmergency(capture, true); //escalate
                } else {
                    this.guidance = "STABLE";
                    Tview.showLogTriageSuccess();
                    Tview.showError("Symptoms improved. Continue monitoring.");
                    Tview.closeDialog();
                }
                logDecision(this.guidance, capture, shouldEscalate);
            });

            return;
        }
        if(Tmodel.isRedFlag(capture)) {this.guidance = "EMERGENCY";
        } else { this.guidance = "REMEDY";}
        Tview.showDecision(redFlag);
    }
    public void startTimer(long n, String childID){ //starts timer for n minutes

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, TriageReceiver.class);
        intent.putExtra("CHILD_ID", childID); //attaches child for parent-side view on recheck

        PendingIntent pendingInt = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        ); //create request to create receiver

        long triggerTime = System.currentTimeMillis() + ( n * 1000);

        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingInt); //set request to open in time
    }


    public void logDecision(String decision, triageCapture capture, boolean escalation){
        Tmodel.logIncident(decision, this.guidance, capture, escalation);
    }

    public void onLogFailure(String s){
        Tview.showLogTriageFailure(s);
    }

    public void onLogSuccess(){
        Tview.showLogTriageSuccess();
    }

    public void loadChildDropdown() {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Tmodel.fetchChildren(userID, this);
    }

    @Override
    public void onChildrenLoaded(List<ChildSpinnerOption> childrenList) {
        Tview.passChildrenToFragment(childrenList);
    }

    @Override
    public void onError(String message) {
        Tview.showError(message);
    }

    public void pullChildren(){
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.Tmodel.fetchChildren(userID, this);

        Tmodel.preloadRescueLogs(userID);
    }

    public void onChildSelected(String userID){
        Tmodel.preloadRescueLogs(userID);
    }
}





