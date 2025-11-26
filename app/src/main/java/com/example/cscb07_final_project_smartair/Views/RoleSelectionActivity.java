package com.example.cscb07_final_project_smartair.Views;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cscb07_final_project_smartair.R;
import com.example.cscb07_final_project_smartair.Presenters.RoleSelectionPresenter;
import com.example.cscb07_final_project_smartair.Views.RoleSelectionView;

public class RoleSelectionActivity extends AppCompatActivity implements RoleSelectionView {

    private RoleSelectionPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_role_selection);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button child = findViewById(R.id.child),
                parent = findViewById(R.id.parent),
                provider = findViewById(R.id.provider);

        presenter = new RoleSelectionPresenter(this);

        child.setOnClickListener(view -> presenter.onChildClick());
        parent.setOnClickListener((view -> presenter.onParentClick()));
        provider.setOnClickListener(view -> presenter.onProviderClick());
    }

    @Override
    public void navigateToChildHome() {
        // navigate to child home page
    }

    @Override
    public void navigateToParentHome() {
        // navigate to parent home page
    }

    @Override
    public void navigateToProviderHome() {
        // navigate to provider home page
    }
}