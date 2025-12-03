package com.example.cscb07_final_project_smartair.Models;

import androidx.annotation.NonNull;

import com.example.cscb07_final_project_smartair.Users.ChildPermissions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProviderReportModel extends BaseModel {
    private FirebaseAuth mAuth;

    public ProviderReportModel() {
        mAuth = FirebaseAuth.getInstance();
    }

    public String getCurrentUserId() {
        if (mAuth.getCurrentUser() != null) {
            return mAuth.getCurrentUser().getUid();
        }

        else {
            return null;
        }
    }

    public interface getProvidersListener{
        void onProvidersLoaded(List<ChildPermissions> providers);
    }
    public void getProviders(String childId, getProvidersListener listener) {
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();

        root.child("users").child("children").child(childId).child("sharingPerms")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {


                        //return empty list on empty snapshot
                        if (!snapshot.exists()) {
                            listener.onProvidersLoaded(new ArrayList<>());
                            return;
                        }

                        //convert format to map
                        GenericTypeIndicator<HashMap<String, ChildPermissions>> t =
                                new GenericTypeIndicator<HashMap<String, ChildPermissions>>() {};

                        Map<String, ChildPermissions> permMap = snapshot.getValue(t); //get db data
                        List<ChildPermissions> fullList = new ArrayList<>();

                        // convert into list
                        if (permMap != null) { //loop through entries to populate list
                            for (Map.Entry<String, ChildPermissions> entry : permMap.entrySet()) {
                                fullList.add(entry.getValue());
                            }
                        }

                        ChildPermissions noneOption = new ChildPermissions();
                        noneOption.provider_name = "None";
                        noneOption.providerID = "NONE";
                        fullList.add(0, noneOption);
                        //send to presenter
                        listener.onProvidersLoaded(fullList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    public ChildPermissions getFilteredPerms(ChildPermissions filters, ChildPermissions selected_provider){
        if(selected_provider.providerID != "NONE") {
            filters.summaryCharts = filters.summaryCharts && selected_provider.summaryCharts;
            filters.triageIncidents = filters.triageIncidents && selected_provider.triageIncidents;
            filters.triggers = filters.triggers && selected_provider.triggers;
            filters.contrSummary = filters.contrSummary && selected_provider.contrSummary;
            filters.symptoms = filters.symptoms && selected_provider.symptoms;
            filters.rescueLogs = filters.rescueLogs && selected_provider.rescueLogs;
        }
        return filters;
    }

}




