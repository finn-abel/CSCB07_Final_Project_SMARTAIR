package com.example.cscb07_final_project_smartair.Presenters;

import com.example.cscb07_final_project_smartair.DataObjects.Invite;
import com.example.cscb07_final_project_smartair.Models.InvitesModel;
import com.example.cscb07_final_project_smartair.Views.InvitesView;

import java.util.ArrayList;
import java.util.List;

public class InvitesPresenter {
    private final InvitesView view;
    private final InvitesModel model;

    private List<String> childIds = new ArrayList<>();
    private List<String> childNames = new ArrayList<>();

    private String currentChildId;

    public InvitesPresenter(InvitesView view) {
        this.view = view;
        this.model = new InvitesModel(this);
    }

    public void loadChildren() {
        model.loadChildren();
    }

    public void onChildrenLoaded(List<String> ids, List<String> names) {
        this.childIds = ids;
        this.childNames = names;

        view.displayChildren(names, ids);

        if (!ids.isEmpty()) {
            onChildSelected(0);
        }
    }

    // ---------------- CHILD SELECTION ----------------
    public void onChildSelected(int index) {
        if (index < 0 || index >= childIds.size()) return;

        currentChildId = childIds.get(index);
        model.loadInvitesForChild(currentChildId);
    }

    public void onInvitesLoaded(List<Invite> invites) {
        view.clearInvites();
        view.displayInvites(invites, childNames);
    }

    public void onGenerateInviteClicked() {
        if (currentChildId != null)
            model.generateInvite(currentChildId);
    }

    public void onInviteGenerated(Invite invite) {
        view.showSuccess("Invite created.");
        model.loadInvitesForChild(currentChildId);
    }

    public void onRevokeClicked(String inviteCode) {
        if (currentChildId != null)
            model.revokeInvite(currentChildId, inviteCode);
    }

    public void onInviteRevoked(String code) {
        view.showSuccess("Invite revoked.");
        model.loadInvitesForChild(currentChildId);
    }

    public void onFailure(String msg) {
        view.showError(msg);
    }
}
