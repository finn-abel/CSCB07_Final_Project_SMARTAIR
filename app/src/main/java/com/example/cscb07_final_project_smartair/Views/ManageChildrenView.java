package com.example.cscb07_final_project_smartair.Views;

import java.util.List;

import com.example.cscb07_final_project_smartair.Users.ChildPermissions;

public interface ManageChildrenView {
    void navigateToAddChild();
    void displayChildren(List<String> names);
    void displayProviders(List<String> names);
    void displayPerms(ChildPermissions perms);
    void showSuccess(String msg);
    void showError(String msg);
}
