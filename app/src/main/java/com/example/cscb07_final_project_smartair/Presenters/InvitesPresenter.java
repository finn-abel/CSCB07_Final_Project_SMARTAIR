package com.example.cscb07_final_project_smartair.Presenters;

import com.example.cscb07_final_project_smartair.Views.InvitesView;
import com.example.cscb07_final_project_smartair.Models.InvitesModel;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class InvitesPresenter {
    private final InvitesView view;
    private final InvitesModel model;
    private List<String> ids, invites;
    private List<Map<String, Long>> invMap;
    private int curChildIndex;
    private String currentChild, currentInv;

    public InvitesPresenter(InvitesView view) {
        this.view = view;
        model = new InvitesModel(this);
    }

    public void loadInvites() {
        model.getInvites();
    }

    public void onInvitesLoaded(List<String> names, List<String> ids, List<Map<String, Long>> invMap) {
        if (names.isEmpty()) {
            onFailure("No children found");
            return;
        }
        this.invMap = invMap;
        this.ids = ids;
        view.displayChildren(names);
    }

    public void onChildSelected(int i) {
        currentChild = ids.get(curChildIndex = i);
        invites = new ArrayList<>(invMap.get(i).keySet());
        view.displayInvites(invites);
    }

    public void onInviteSelected(int i) {
        currentInv = invites.get(i);
        view.setText("Valid until " + new SimpleDateFormat("yyyy-MM-dd").format(new Date(invMap.get(curChildIndex).get(currentInv))));
    }

    public void onGenClicked() {
        model.generateInvite(currentChild);
    }

    public void onRevClicked() {
        model.revokeInvite(currentChild, currentInv);
    }

    public void onInviteGenerated(String inv, long time) {
        invMap.get(curChildIndex).put(inv, time);
        view.addInvite(inv);
        view.showSuccess("Successfully generated invite");
    }

    public void onInviteRevoked(String inv) {
        view.removeInvite(inv);
        view.showSuccess("Successfully revoked invite");
    }

    public void onSuccess(String msg) {
        view.showSuccess(msg);
    }

    public void onFailure(String msg) {
        view.showError(msg);
    }
}
