package com.example.cscb07_final_project_smartair.Presenters;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.cscb07_final_project_smartair.Helpers.ProviderReportGenerator;
import com.example.cscb07_final_project_smartair.Models.BaseModel;
import com.example.cscb07_final_project_smartair.Models.ParentHomeModel;
import com.example.cscb07_final_project_smartair.Models.ProviderReportModel;
import com.example.cscb07_final_project_smartair.Users.ChildPermissions;
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
    public ChildPermissions selected_provider;
    public String role;
    private HashMap<String, ChildSpinnerOption> children = new HashMap<String, ChildSpinnerOption>();


    public ProviderReportPresenter(ProviderReportView view){
        this.view = view;
        this.model = new ProviderReportModel();
        this.selected_provider = null;
    }

    public void onChildSelectedProvider(String childId) {
        if (childId == null || childId.isEmpty()) return;

        model.getProviders(childId, new ProviderReportModel.getProvidersListener() {
            @Override
            public void onProvidersLoaded(List<ChildPermissions> providers) {
                view.displayProviders(providers);
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


    public void onProviderSelected(ChildPermissions selected) {
        this.selected_provider = selected;
    }

    public void onGenerateClickedParent(ProviderReportSelectionActivity activity, String childID, int months,
                                  ChildPermissions filters){
        filters = model.getFilteredPerms(filters,selected_provider);
        new ProviderReportGenerator().generateReport(activity, childID, months,
                selected_provider.providerID, filters);
    }

}
