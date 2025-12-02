package com.example.cscb07_final_project_smartair.Presenters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.cscb07_final_project_smartair.Models.BaseModel;
import com.example.cscb07_final_project_smartair.Models.ManageChildrenModel;
import com.example.cscb07_final_project_smartair.Users.ChildSpinnerOption;
import com.example.cscb07_final_project_smartair.Views.ManageChildrenView;
import com.example.cscb07_final_project_smartair.Users.ChildPermissions;
import com.google.firebase.auth.FirebaseAuth;


public class ManageChildrenPresenter implements BaseModel.ChildFetchListener,
                                                    ManageChildrenModel.GuidanceListener{
    private final ManageChildrenView view;
    private final ManageChildrenModel model;
    private String selectedChildId;
    private List<ChildPermissions> providerList;
    private Map<String, ChildPermissions> permMap;
    private ChildPermissions selectedProviderPerms;

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

    // called when child selection fails
    public void onFailure(String msg) {
        view.showError(msg);
    }

    public void onChildSelected(String childId) {
        this.selectedChildId = childId;
        this.permMap = null;
        this.selectedProviderPerms = null;
        view.updateUI(0); // hide notices during loading
        model.getProviders(childId);
        model.getPefDetails(childId, this); //obtain guidance
    }

    public void onProvidersLoaded(List<ChildPermissions> providers) {
        this.providerList = providers;

        this.permMap = new HashMap<>();
        for (ChildPermissions p : providers) {
            if (p.providerID != null) {
                permMap.put(p.providerID, p);
            }
        }

        if (providers.isEmpty()) {
            view.updateUI(1); //no providers
            return;
        }

        //providers found
        view.updateUI(2);

        view.displayProviders(providers);  //populate spinner

        if (!providers.isEmpty()) {
            onProviderSelected(0);
        }
    }

    public void onProviderSelected(int index) {
        if (providerList == null || index < 0 || index >= providerList.size()) return;

        this.selectedProviderPerms = providerList.get(index);

        view.displayPerms(selectedProviderPerms);
    }

    public void onAddChildClicked() {
        view.navigateToAddChild();
    }

    public void onRescueChanged(boolean val) {
        if (selectedProviderPerms != null) {
            selectedProviderPerms.rescueLogs = val;
            model.setPerms(selectedChildId, permMap);
        }
    }

    public void onAdherenceChanged(boolean val) {
        if (selectedProviderPerms != null) {
            selectedProviderPerms.contrSummary = val;
            model.setPerms(selectedChildId, permMap);
        }
    }

    public void onSymptomsChanged(boolean val) {
        if (selectedProviderPerms != null) {
            selectedProviderPerms.symptoms = val;
            model.setPerms(selectedChildId, permMap);
        }
    }

    public void onTriggersChanged(boolean val) {
        if (selectedProviderPerms != null) {
            selectedProviderPerms.triggers = val;
            model.setPerms(selectedChildId, permMap);
        }
    }

    public void onPefChanged(boolean val) {
        if (selectedProviderPerms != null) {
            selectedProviderPerms.pef = val;
            model.setPerms(selectedChildId, permMap);
        }
    }

    public void onTriageChanged(boolean val) {
        if (selectedProviderPerms != null) {
            selectedProviderPerms.triageIncidents = val;
            model.setPerms(selectedChildId, permMap);
        }
    }

    public void onSummaryChanged(boolean val) {
        if (selectedProviderPerms != null) {
            selectedProviderPerms.summaryCharts = val;
            model.setPerms(selectedChildId, permMap);
        }
    }


    //PEF IMPLEMENTATION//
    @Override
    public void onGuidanceLoaded(String red, String yellow, String green, float pb) {
        view.populatePefFields(red, yellow, green, pb);
    }

    public void onSavePefClicked(String red, String yellow, String green, String pbString) {
        float pb = 0;
        try {
            pb = Float.parseFloat(pbString);
        } catch (NumberFormatException e) {
            view.showError("Invalid Personal Best number");
            return;
        }

        model.savePefDetails(selectedChildId, red, yellow, green, pb);
    }

    public void onSuccess(String msg){
        view.showError(msg);
    }
}
