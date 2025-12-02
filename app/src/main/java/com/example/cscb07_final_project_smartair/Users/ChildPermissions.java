package com.example.cscb07_final_project_smartair.Users;

import androidx.annotation.NonNull;

public class ChildPermissions {
    public String provider_name;
    public String providerID;
    public boolean rescueLogs;
    public boolean contrSummary;
    public boolean symptoms;
    public boolean triggers;
    public boolean pef;
    public boolean triageIncidents;
    public boolean summaryCharts;

    public ChildPermissions(){}

    public ChildPermissions(boolean rescueLogs, boolean contrSummary, boolean symptoms,
                            boolean triggers, boolean pef, boolean triageIncidents,
                            boolean summaryCharts, String provider_name, String providerID){
        this.contrSummary = contrSummary;
        this.pef = pef;
        this.provider_name = provider_name;
        this.providerID = providerID;
        this.rescueLogs = rescueLogs;
        this.summaryCharts = summaryCharts;
        this.symptoms = symptoms;
        this.triageIncidents = triageIncidents;
        this.triggers = triggers;
    }

    @NonNull
    @Override
    public String toString(){
        return this.provider_name;
    }

}
