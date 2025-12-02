package com.example.cscb07_final_project_smartair.Presenters;

import com.example.cscb07_final_project_smartair.Models.BaseModel;
import com.example.cscb07_final_project_smartair.Models.ParentHomeModel;
import com.example.cscb07_final_project_smartair.Models.ProviderReportModel;
import com.example.cscb07_final_project_smartair.Users.ChildSpinnerOption;
import com.example.cscb07_final_project_smartair.Views.ParentHomeView;
import com.example.cscb07_final_project_smartair.Views.ProviderReportView;
import com .example.cscb07_final_project_smartair.Views.ProviderReportSelectionActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProviderReportPresenter {
    private final ProviderReportView view;
    public ProviderReportModel model;
    private HashMap<String, ChildSpinnerOption> children = new HashMap<String, ChildSpinnerOption>();


    public ProviderReportPresenter(ProviderReportView view){
        this.view = view;
        this.model = new ProviderReportModel();
    }

    public void onChildSelectedProvider(String childId) {
        if (childId == null || childId.isEmpty()) return;

        model.fetchProvidersForChild(childId, new BaseModel.ProviderFetchListener() {
            @Override
            public void onProvidersLoaded(List<BaseModel.ProviderSpinnerOption> providers) {
                view.displayProviders(providers);
            }

            @Override
            public void onError(String message) {

            }
        });
    }

    public void loadChildrenProvider() {
        String parentId = FirebaseAuth.getInstance().getCurrentUser().getUid();;

        model.fetchChildren(parentId, new BaseModel.ChildFetchListener() {

            @Override
            public void onChildrenLoaded(List<ChildSpinnerOption> childList) {
                children.clear();
                for (ChildSpinnerOption child : childList) {
                    children.put(child.userID, child);
                }
                view.displayChildren(childList);
            }

            @Override
            public void onError(String message) {
            }
        });
    }
}
