package com.example.cscb07_final_project_smartair.Views;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.cscb07_final_project_smartair.R;

public class CheckupFragment extends DialogFragment {

    private TriageView listener;

    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        if (context instanceof TriageView){
            listener = (TriageView) context; //connect activity to dialog
        } else {
            throw new RuntimeException("Activity does not implement TriageView interface");
        }//ensure activity has interface
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        return inflater.inflate(R.layout.triage_checkup,container, false);
    }//display initial dialog

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        CheckBox speak = view.findViewById(R.id.checkBox_speak);
        CheckBox chest = view.findViewById(R.id.checkBox_chest);
        CheckBox lips = view.findViewById(R.id.checkBox_lips);

        view.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onFormSubmit(speak.isChecked(),lips.isChecked(),chest.isChecked());
            }//on click, evaluate symptoms checked and submit form
        });

        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.closeDialog();
            }//on click, close dialog and return to activity
        });
    }
}
