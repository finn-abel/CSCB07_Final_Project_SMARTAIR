package com.example.cscb07_final_project_smartair.Presenters;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;

import com.example.cscb07_final_project_smartair.Models.PEFmodel;
import com.example.cscb07_final_project_smartair.Views.MainActivityView;
import com.example.cscb07_final_project_smartair.Views.PEFView;

public class PEFPresenter {

    PEFView view;
    PEFmodel model;

    public PEFPresenter(PEFView view){
        this.model = new PEFmodel(this);
        this.view = view;
    }

    public void onEnterClicked(){

        String preText = view.getPre();
        String postText = view.getPost();
        String currentText = view.getCurrent();

        if(currentText.isEmpty()){
            view.showPEFError("Current PEF cannot be empty");
            return;
        }//cancel on blank PEF

        try {
            Float current = Float.parseFloat(currentText);
            Float pre = preText.isEmpty() ? null : Float.parseFloat(preText);
            Float post = postText.isEmpty() ? null : Float.parseFloat(postText);
            //convert to null if empty

            model.logPEF(pre,post,current);
        } catch (Exception e) {
            view.showFailure(e.getMessage());
        }
    }

    public void onLogSuccess(){
        view.showSuccess();
    }

    public void onLogFailure(String s){
        view.showFailure(s);
    }
}
