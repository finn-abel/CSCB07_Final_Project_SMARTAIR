package com.example.cscb07_final_project_smartair.Views;

import java.util.List;

public interface InvitesView {
    void displayChildren(List<String> children);
    void displayInvites(List<String> invites);
    void addInvite(String invite);
    void removeInvite(String invite);
    void setText(String s);
    void showSuccess(String msg);
    void showError(String msg);
}
