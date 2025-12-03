package com.example.cscb07_final_project_smartair.Views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cscb07_final_project_smartair.DataObjects.Invite;
import com.example.cscb07_final_project_smartair.Presenters.InvitesPresenter;
import com.example.cscb07_final_project_smartair.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class InvitesActivity extends BaseParentActivity implements InvitesView {
    private Spinner spinnerChildren;
    private LinearLayout containerInvites;
    private Button btnCreate;
    private InvitesPresenter presenter;
    private List<String> childIds;
    private List<String> childNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invites);

        presenter = new InvitesPresenter(this);

        spinnerChildren = findViewById(R.id.spinnerChildSelector);
        containerInvites = findViewById(R.id.inviteContainer);
        btnCreate = findViewById(R.id.btnCreateInvite);

        btnCreate.setOnClickListener(v -> presenter.onGenerateInviteClicked());

        presenter.loadChildren();
    }

    @Override
    public void displayChildren(List<String> names, List<String> ids) {
        this.childIds = ids;
        this.childNames = names;

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerChildren.setAdapter(adapter);

        spinnerChildren.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int pos, long id) {
                presenter.onChildSelected(pos);
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    @Override
    public void clearInvites() {
        containerInvites.removeAllViews();
    }

    @Override
    public void displayInvites(List<Invite> invites, List<String> childNames) {
        LayoutInflater inflater = LayoutInflater.from(this);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        if (invites == null || invites.isEmpty()) {
            TextView tv = new TextView(this);
            tv.setText("No invites yet.");
            tv.setTextSize(16f);
            tv.setPadding(16, 24, 16, 24);
            containerInvites.addView(tv);
            return;
        }

        for (Invite inv : invites) {
            View row = inflater.inflate(R.layout.item_invite, containerInvites, false);
            ((TextView) row.findViewById(R.id.tvInviteCode))
                    .setText(inv.inviteCode);
            ((TextView) row.findViewById(R.id.tvChildName))
                    .setText("Child: " + spinnerChildren.getSelectedItem().toString());
            ((TextView) row.findViewById(R.id.tvExpiresAt))
                    .setText("Expires: " + sdf.format(inv.expiresAt));
            Button revoke = row.findViewById(R.id.btnRevoke);
            revoke.setOnClickListener(v -> presenter.onRevokeClicked(inv.inviteCode));

            containerInvites.addView(row);
        }
    }

    @Override
    public void showSuccess(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void showError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
