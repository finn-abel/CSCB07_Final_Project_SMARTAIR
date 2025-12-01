package com.example.cscb07_final_project_smartair.Views;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.cscb07_final_project_smartair.DataObjects.triageCapture;
import com.example.cscb07_final_project_smartair.R;
import com.example.cscb07_final_project_smartair.Users.ChildSpinnerOption;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class TriageFragment extends DialogFragment {

    private View checkupView;
    private View decisionView;
    private View remedyView;
    private TextView remedyHeader;
    private TextView remedyData;
    private Button emergencyButton;
    private Button remedyButton;
    private TriageView listener;

    public String childID;
    private Spinner child_select;

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
        return inflater.inflate(R.layout.triage_layout,container, false);
    }//display initial dialog

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        this.checkupView = view.findViewById(R.id.checkup_layout);
        this.decisionView = view.findViewById(R.id.decision_layout);
        this.remedyView = view.findViewById(R.id.remedy_layout);

        if (this.checkupView != null) checkupView.setVisibility(View.VISIBLE);
        if (this.decisionView != null) decisionView.setVisibility(View.GONE);
        if (this.remedyView != null) remedyView.setVisibility(View.GONE);

        TextView select_child_prompt = view.findViewById(R.id.select_child_prompt_triage);
        this.child_select = view.findViewById(R.id.select_child_spinner);

        if(!isParent()){
            select_child_prompt.setVisibility(View.GONE);
            child_select.setVisibility(View.GONE);
        } else {
            listener.getChildren();
        }



        this.emergencyButton= view.findViewById(R.id.emergencyButton);
        this.remedyButton = view.findViewById(R.id.homeRemedyButton);
        this.remedyHeader = view.findViewById(R.id.remedy_header);
        this.remedyData = view.findViewById(R.id.remedy_data);

        CheckBox speak = view.findViewById(R.id.checkBox_speak);
        CheckBox chest = view.findViewById(R.id.checkBox_chest);
        CheckBox lips = view.findViewById(R.id.checkBox_lips);
        CheckBox rescue = view.findViewById(R.id.share_rescue_checkbox);
        EditText pef = view.findViewById(R.id.optional_pef_field);
        EditText timer_length = view.findViewById(R.id.triage_timer_length);

        view.findViewById(R.id.submit_triage).setOnClickListener(v -> {
                triageCapture capture = new triageCapture(
                        speak.isChecked(),
                        lips.isChecked(),
                        chest.isChecked(),
                        getNum(pef),
                        null,
                        rescue.isChecked()
                );
                if(isParent()){
                    Object o = child_select.getSelectedItem();
                    ChildSpinnerOption option = (ChildSpinnerOption) o;
                    capture.userID = option.userID;
                } else {
                    capture.userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                }
                listener.onFormSubmit(capture);
                //on click, evaluate symptoms checked and submit form
        });

        view.findViewById(R.id.cancel_triage).setOnClickListener(v -> {
                listener.closeDialog();//on click, close dialog and return to activity
        });

        view.findViewById(R.id.homeRemedyButton).setOnClickListener(v -> {
            triageCapture capture = new triageCapture(
                    speak.isChecked(),
                    lips.isChecked(),
                    chest.isChecked(),
                    getNum(pef),
                    null,
                    rescue.isChecked()
            );
            if(isParent()){
                Object o = child_select.getSelectedItem();
                ChildSpinnerOption option = (ChildSpinnerOption) o;
                capture.userID = option.userID;
            } else {
                capture.userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            }
            long timer = getLong(timer_length);

            listener.showTimerStart(capture, timer);
        });

        view.findViewById(R.id.emergencyButton).setOnClickListener(v -> {
            triageCapture capture = new triageCapture(
                    speak.isChecked(),
                    lips.isChecked(),
                    chest.isChecked(),
                    getNum(pef),
                    null,
                    rescue.isChecked()
            );
            if(isParent()){ //use spinner option for parent
                Object o = child_select.getSelectedItem();
                ChildSpinnerOption option = (ChildSpinnerOption) o;
                capture.userID = option.userID;
            } else { //use current user for child
                capture.userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            }
                listener.callEmergency(capture, false);
        });

    }

    public void morphToDecision(boolean isRedFlag) {
        // update text
        if (isRedFlag) {
            int color = Color.parseColor("#c40e0e"); //danger red
            emergencyButton.setText("CRITICAL: Call Emergency Services");
            emergencyButton.setBackgroundTintList(ColorStateList.valueOf(color));
        } else {
            int color = Color.parseColor("#0cba06"); //safe green
            remedyButton.setText("STABLE: Use Home Remedies");
            remedyButton.setBackgroundTintList(ColorStateList.valueOf(color));
        }

        // swap visibility
        checkupView.setVisibility(View.GONE);
        decisionView.setVisibility(View.VISIBLE);
    }

    public void morphToRemedy(String s, String level) {

        // update text

        this.remedyData.setText(s);

        if (level.equals("RED")) {
            this.remedyHeader.setText("Red Zone");
            this.remedyHeader.setTextColor(getResources().getColor(R.color.danger_red));
        } else if (level.equals("YELLOW")){
            this.remedyHeader.setText("Yellow Zone");
            this.remedyHeader.setTextColor(getResources().getColor(R.color.warning_yellow));
        } else if (level.equals("GREEN")) {
            this.remedyHeader.setText("Green Zone");
            this.remedyHeader.setTextColor(getResources().getColor(R.color.safe_green));
        } else {
            this.remedyHeader.setText("Error");
        }

        // swap visibility


        decisionView.setVisibility(View.GONE);
        remedyView.setVisibility(View.VISIBLE);
    }
    public Float getNum(EditText num){
        String numString = num.getText().toString().trim();
        if(numString.isEmpty()){ return null;
        } else { return Float.parseFloat(numString);}
    }

    public long getLong(EditText num){
        String numString = num.getText().toString().trim();
        if(numString.isEmpty()){ return 10L;
        } else { return Long.parseLong(numString);}
    }

    public boolean isParent() {
        return listener.isParent();
    }

    public void updateSpinner(List<ChildSpinnerOption> childrenList) {
        ArrayAdapter<ChildSpinnerOption> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item,
                childrenList);  //create adapter for spinner selections
        child_select.setAdapter(adapter); //set adapter

        if (this.childID != null) {

            // loop through list for matching id on recheck
            for (int i = 0; i < childrenList.size(); i++)
                if (childrenList.get(i).userID.equals(this.childID)) {
                    child_select.setSelection(i);
                    child_select.setEnabled(false); //stop parent from changing on recheck
                    break; // end loop
                }
            }

        child_select.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ChildSpinnerOption selectedChild = (ChildSpinnerOption) parent.getItemAtPosition(position);
                String childID = selectedChild.userID;
                if (listener != null) {
                    listener.onChildSelected(childID);
                }
            } //get currently selected item

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}
