package com.example.cscb07_final_project_smartair.Receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class TriageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent){
        triggerNotification(context);     //send notification
    }

    private void triggerNotification(Context context){
        String chID = "TRIAGE_RECHECK_CHANNEL"; //channel ID

        Intent appInt = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        if(appInt != null){
            appInt.putExtra("SHOW_TRIAGE_RECHECK", true);
            appInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else {
            return;
        }//setup tap action

        PendingIntent pendingInt = PendingIntent.getActivity(
                context, 0, appInt,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        ); //create pending intent for app opening

        NotificationManager notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //get notification manager

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, chID)
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setContentTitle("SMART AIR")
                .setContentText("Still having trouble breathing? Tap to re-check your symptoms")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingInt)
                .setAutoCancel(true);
        //build notification

        notifManager.notify(1000, builder.build()); //send notification


    }


}
