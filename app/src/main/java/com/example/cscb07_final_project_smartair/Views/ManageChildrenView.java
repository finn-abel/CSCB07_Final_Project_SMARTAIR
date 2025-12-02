package com.example.cscb07_final_project_smartair.Views;

import java.util.List;

import com.example.cscb07_final_project_smartair.Users.ChildPermissions;
import com.example.cscb07_final_project_smartair.Users.ChildSpinnerOption;

public interface ManageChildrenView {
    void navigateToAddChild();
    void displayProviders(List<String> names);
    void displayPerms(ChildPermissions perms);
    void showSuccess(String msg);
    void showError(String msg);

    void displayChildren(List<ChildSpinnerOption> childrenList);
}
