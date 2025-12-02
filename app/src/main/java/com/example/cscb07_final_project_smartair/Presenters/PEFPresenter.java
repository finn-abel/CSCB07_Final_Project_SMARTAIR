package com.example.cscb07_final_project_smartair.Presenters;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;

import com.example.cscb07_final_project_smartair.DataObjects.pefCapture;
import com.example.cscb07_final_project_smartair.Models.BaseModel;
import com.example.cscb07_final_project_smartair.Models.PEFmodel;
import com.example.cscb07_final_project_smartair.Models.TriageModel;
import com.example.cscb07_final_project_smartair.Users.ChildSpinnerOption;
import com.example.cscb07_final_project_smartair.Views.MainActivityView;
import com.example.cscb07_final_project_smartair.Views.PEFView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class PEFPresenter implements BaseModel.ChildFetchListener, PEFmodel.getPBListener {

    PEFView view;
    PEFmodel model;
    float pb_pef;
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

            pefCapture capture = new pefCapture(
                    null,
                    pre,
                    post,
                    current,
                    this.pb_pef
            );

            capture.childID = view.isParent() ? view.getSpinnerOption().userID :
                    FirebaseAuth.getInstance().getCurrentUser().getUid();

            model.logPEF(capture);
        } catch (Exception e) {
            view.showFailure(e.getMessage());
        }
    }

    public void getChildren(){
        BaseModel Bmodel = new BaseModel();
        Bmodel.fetchChildren(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                this);
    }

    @Override
    public void onChildrenLoaded(List<ChildSpinnerOption> childrenList){
        view.updateSpinner(childrenList);
    }

    @Override
    public void onError(String message) {
        view.showError(message);
    }
    public void onLogSuccess(){
        view.showSuccess();
    }

    public void onLogFailure(String s){
        view.showFailure(s);
    }

    @Override
    public void onPBRetrieved(float pb){
        this.pb_pef=pb;
    }

    public void getPEFpb(String childID){
        model.getPB(childID, this);
    }
}
