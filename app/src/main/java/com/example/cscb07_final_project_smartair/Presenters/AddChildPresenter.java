package com.example.cscb07_final_project_smartair.Presenters;

import com.example.cscb07_final_project_smartair.Models.AddChildModel;
import com.example.cscb07_final_project_smartair.Views.AddChildView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class AddChildPresenter {
    private final AddChildView view;
    private final AddChildModel model;

    public AddChildPresenter(AddChildView view) {
        this.view = view;
        this.model = new AddChildModel(this);
    }

    public void onAddChildButtonClicked() {
        String name = view.getName(), email = view.getEmail(), password = view.getPassword(), dob = view.getDob(),
                notes = view.getNotes(), pefString = view.getPEF();
        long date;
        float pef;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            date = sdf.parse(dob).getTime();
        } catch (ParseException e) {
            view.showSignUpFailure("Date format must be YYYY-MM-DD");
            return;
        }
        if(name.isEmpty()||email.isEmpty()||password.isEmpty()||dob.isEmpty()){
            view.showSignUpFailure("Please fill in necessary fields");
            return;
        }
        try{
            pef = (pefString.isEmpty()) ? 300 : Float.parseFloat(pefString);
        } catch (NumberFormatException e) {
            view.showSignUpFailure("Please enter a number for PEF");
            return;
        }
        model.createUser(name, email, password, date, notes,pef);
    }

    public void onSignUpSuccess() {
        if (view != null) {
            view.showSignUpSuccess("Account created successfully! Please log in.");
            view.navigateToManageScreen();
        }
    }

    public void onSignUpFailure(String errorMessage) {
        if (view != null) {
            view.showSignUpFailure(errorMessage);
        }
    }
}
