package com.example.cscb07_final_project_smartair.Views;

import java.util.List;

import com.example.cscb07_final_project_smartair.Users.ChildPermissions;
import com.example.cscb07_final_project_smartair.Users.ChildSpinnerOption;
import com.example.cscb07_final_project_smartair.Users.pefGuidance;

public interface ManageChildrenView {
    void navigateToAddChild();

    void displayProviders(List<ChildPermissions> providerList);

    void displayPerms(ChildPermissions perms);
    void showSuccess(String msg);
    void showError(String msg);

    void displayChildren(List<ChildSpinnerOption> childrenList);

    void updateUI(int state);

    void populatePefFields(String red, String yellow, String green, float pb);
}
