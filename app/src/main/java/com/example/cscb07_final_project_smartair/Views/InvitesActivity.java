package com.example.cscb07_final_project_smartair.Views;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cscb07_final_project_smartair.R;
import com.example.cscb07_final_project_smartair.Presenters.InvitesPresenter;

import java.util.List;

public class InvitesActivity extends AppCompatActivity implements InvitesView {
    private Spinner children, invites;
    private ArrayAdapter<String> adapter;
    private Button btnGen, btnRev;
    private TextView valid;
    private InvitesPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_invites);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        presenter = new InvitesPresenter(this);

        children = findViewById(R.id.children);
        invites = findViewById(R.id.invites);
        valid = findViewById(R.id.valid);
        btnGen = findViewById(R.id.genInv);
        btnRev = findViewById(R.id.revInv);
        btnGen.setOnClickListener(view -> presenter.onGenClicked());
        btnRev.setOnClickListener(view -> presenter.onRevClicked());

        presenter.loadInvites();
    }

    @Override
    public void displayChildren(List<String> names) {
        ArrayAdapter<String> childAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, names);
        childAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        children.setAdapter(childAdapter);
        children.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
                presenter.onChildSelected(i);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
        children.setSelection(0);
    }

    @Override
    public void displayInvites(List<String> names) {
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        invites.setAdapter(adapter);
        invites.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
                presenter.onInviteSelected(i);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    public void setText(String s) {
        valid.setText(s);
    }

    @Override
    public void addInvite(String invite) {
        adapter.add(invite);
    }

    @Override
    public void removeInvite(String invite) {
        adapter.remove(invite);
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