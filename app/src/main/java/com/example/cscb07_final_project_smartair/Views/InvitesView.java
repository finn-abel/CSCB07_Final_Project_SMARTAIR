package com.example.cscb07_final_project_smartair.Views;

import com.example.cscb07_final_project_smartair.DataObjects.Invite;

import java.util.List;

public interface InvitesView {
    void displayChildren(List<String> childNames, List<String> childIds);
    void clearInvites();
    void displayInvites(List<Invite> invites, List<String> childNames);

    void showSuccess(String msg);
    void showError(String msg);
}
