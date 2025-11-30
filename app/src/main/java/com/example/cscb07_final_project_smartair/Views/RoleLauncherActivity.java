package com.example.cscb07_final_project_smartair.Views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cscb07_final_project_smartair.Presenters.RoleLauncherPresenter;
import com.example.cscb07_final_project_smartair.R;
import com.example.cscb07_final_project_smartair.Presenters.RoleSelectionPresenter;
import com.example.cscb07_final_project_smartair.Views.RoleSelectionView;

public class RoleLauncherActivity extends BaseActivity implements RoleLauncherView {

    private RoleLauncherPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_role_launcher);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button child = findViewById(R.id.child),
                parent = findViewById(R.id.parent),
                provider = findViewById(R.id.provider);

        presenter = new RoleLauncherPresenter(this);

        child.setOnClickListener(view -> presenter.onChildClick());
        parent.setOnClickListener((view -> presenter.onParentClick()));
        provider.setOnClickListener(view -> presenter.onProviderClick());
    }

    @Override
    public void navigateToChildLogin() {
        startActivity(new Intent(this, ChildLoginActivity.class));
    }

    @Override
    public void navigateToGeneralLogin() {
        startActivity(new Intent(this, LoginActivity.class));
    }

}