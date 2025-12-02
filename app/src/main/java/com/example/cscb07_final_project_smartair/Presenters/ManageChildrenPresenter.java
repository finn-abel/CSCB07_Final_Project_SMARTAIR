package com.example.cscb07_final_project_smartair.Presenters;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;

import com.example.cscb07_final_project_smartair.Models.BaseModel;
import com.example.cscb07_final_project_smartair.Models.ManageChildrenModel;
import com.example.cscb07_final_project_smartair.Users.ChildSpinnerOption;
import com.example.cscb07_final_project_smartair.Views.ManageChildrenView;
import com.example.cscb07_final_project_smartair.Users.Child;
import com.example.cscb07_final_project_smartair.Users.ChildPermissions;
import com.google.firebase.auth.FirebaseAuth;


public class ManageChildrenPresenter implements BaseModel.ChildFetchListener{
    private final ManageChildrenView view;
    private final ManageChildrenModel model;
    private List<String> childIds, providers;
    private Map<String, ChildPermissions> permMap;
    private ChildPermissions perms;
    private String selectedChildId, selectedProvider;

    public ManageChildrenPresenter(ManageChildrenView view) {
        this.view = view;
        this.model = new ManageChildrenModel(this);
    }

    public void loadChildren() {
        model.fetchChildren(FirebaseAuth.getInstance().getCurrentUser().getUid(), this);
    }

    @Override
    public void onChildrenLoaded(List<ChildSpinnerOption> childrenList) {
        view.displayChildren(childrenList);
    }

    @Override
    public void onError(String message){
        view.showError(message);
    }

    public void onProvidersLoaded(Child child) {
        permMap = child.sharingPerms;
        Set<String> providerSet = permMap.keySet();
        providers = new ArrayList<>(providerSet);
        view.displayProviders(providers);
    }

    // Called when child selection fails
    public void onFailure(String msg) {
        view.showError(msg);
    }

    public void onChildSelected(String childId) {
        this.selectedChildId = childId;
        loadProviders();
    }

    public void onProviderSelected(int index) {
        if (index < 0 || index >= permMap.size()) {
            view.showError("Invalid child selection.");
            return;
        }
        selectedProvider = providers.get(index);
        loadPerms();
    }

    public void loadProviders() {
        if (selectedChildId == null) {
            view.showError("No child selected.");
            return;
        }
        model.getProviders(selectedChildId);
    }

    public void loadPerms() {
        if (selectedProvider == null) {
            view.showError("No provider selected.");
            return;
        }
        perms = permMap.get(selectedProvider);
        view.displayPerms(perms);
    }

    public void onAddChildClicked() {
        view.navigateToAddChild();
    }

    public void onRescueChanged(boolean val) {
        perms.rescueLogs = val;
        model.setPerms(selectedChildId, permMap);
    }

    public void onAdherenceChanged(boolean val) {
        perms.contrSummary = val;
        model.setPerms(selectedChildId, permMap);
    }

    public void onSymptomsChanged(boolean val) {
        perms.symptoms = val;
        model.setPerms(selectedChildId, permMap);
    }

    public void onTriggersChanged(boolean val) {
        perms.triggers = val;
        model.setPerms(selectedChildId, permMap);
    }

    public void onPefChanged(boolean val) {
        perms.pef = val;
        model.setPerms(selectedChildId, permMap);
    }

    public void onTriageChanged(boolean val) {
        perms.triageIncidents = val;
        model.setPerms(selectedChildId, permMap);
    }

    public void onSummaryChanged(boolean val) {
        perms.summaryCharts = val;
        model.setPerms(selectedChildId, permMap);
    }
}
