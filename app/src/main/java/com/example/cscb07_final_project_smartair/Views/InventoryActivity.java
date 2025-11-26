package com.example.cscb07_final_project_smartair.Views;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cscb07_final_project_smartair.R;
import com.example.cscb07_final_project_smartair.Models.Items.InventoryItem;
import com.example.cscb07_final_project_smartair.Presenters.InventoryPresenter;

import java.util.List;

public class InventoryActivity extends AppCompatActivity implements InventoryView {

    private InventoryPresenter presenter;
    private final String childId = "child_001"; // TODO: actually get the child id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        presenter = new InventoryPresenter(this);
        presenter.loadInventory(childId);

        findViewById(R.id.backButton).setOnClickListener(v ->
                presenter.onBackClicked()
        );
    }

    @Override
    public void navigateBack() {
        finish();
    }

    @Override
    public void showInventory(List<InventoryItem> items) {
        // TODO: recycler view binding
    }
    // TODO: add inventory addition
    @Override
    public void showSuccess(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}

