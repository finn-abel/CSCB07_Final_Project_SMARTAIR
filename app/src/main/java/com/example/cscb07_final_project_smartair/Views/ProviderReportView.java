package com.example.cscb07_final_project_smartair.Views;

import com.example.cscb07_final_project_smartair.Models.BaseModel;
import com.example.cscb07_final_project_smartair.Users.ChildPermissions;
import com.example.cscb07_final_project_smartair.Users.ChildSpinnerOption;

import java.util.List;

public interface ProviderReportView {

    public void displayChildren(List<ChildSpinnerOption> names);
    void displayProviders(List<ChildPermissions> providers);
}
