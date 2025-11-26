package com.example.cscb07_final_project_smartair.Views;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.cscb07_final_project_smartair.R;

public class DecisionFragment extends DialogFragment {


    private TriageView listener;
    public boolean isRedFlag = false;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof TriageView) {
            listener = (TriageView) context; //connect activity to dialog
        } else {
            throw new RuntimeException("Activity does not implement TriageView interface");
        }//ensure activity has interface
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        return inflater.inflate(R.layout.triage_decision,container, false);
    }//show layout

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        TextView emergencyLabel = view.findViewById(R.id.emergencyLabel);
        TextView remedyLabel = view.findViewById(R.id.remedyLabel);

        if(this.isRedFlag){ //if critical symptom is found
            emergencyLabel.setText("RECOMMENDED: Call emergency services");
        } else { //if no critical symptoms
            remedyLabel.setText("RECOMMENDED: Conduct at-home remedies");
        }

        view.findViewById(R.id.emergencyButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.callEmergency(); //launch phone
            }
        }); //on emergency click

        view.findViewById(R.id.homeRemedyButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.showTimerStart();
            }
        }); //on remedy click

    }


}
