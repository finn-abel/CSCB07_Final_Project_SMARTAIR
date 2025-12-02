package com.example.cscb07_final_project_smartair.Presenters;

import com.example.cscb07_final_project_smartair.Models.BaseModel;
import com.example.cscb07_final_project_smartair.Models.ParentHomeModel;
import com.example.cscb07_final_project_smartair.Models.ProviderReportModel;
import com.example.cscb07_final_project_smartair.Users.ChildSpinnerOption;
import com.example.cscb07_final_project_smartair.Views.ParentHomeView;
import com.example.cscb07_final_project_smartair.Views.ProviderReportView;
import com .example.cscb07_final_project_smartair.Views.ProviderReportSelectionActivity;

import java.util.ArrayList;
import java.util.List;

public class ProviderReportPresenter {
    private final ProviderReportView view;
    public ProviderReportModel model;
    private List<ChildSpinnerOption> children = new ArrayList<>();

    public ProviderReportPresenter(ProviderReportView view){
        this.view = view;
        this.model = new ProviderReportModel();
    }

    public void onChildSelectedProvider(int index) {
        if (index < 0 || index >= children.size()) return;

        ChildSpinnerOption selected = children.get(index);
    }

    public void loadChildrenProvider() {
        String parentId = model.getCurrentUserId();

        model.fetchChildren(parentId, new BaseModel.ChildFetchListener() {

            @Override
            public void onChildrenLoaded(List<ChildSpinnerOption> childList) {
                children = childList;
                view.displayChildren(childList);
            }

            @Override
            public void onError(String message) {
                // Optional: handle errors, e.g. Toast
            }
        });
    }
}
