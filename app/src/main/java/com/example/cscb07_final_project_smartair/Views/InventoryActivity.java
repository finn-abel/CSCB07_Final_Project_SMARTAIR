package com.example.cscb07_final_project_smartair.Views;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;

import com.example.cscb07_final_project_smartair.DataObjects.InventoryItem;
import com.example.cscb07_final_project_smartair.Presenters.InventoryPresenter;
import com.example.cscb07_final_project_smartair.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class InventoryActivity extends BaseActivity {

    private InventoryPresenter presenter;

    private Spinner childSpinner;
    private LinearLayout inventoryContainer;

    private Dialog inventoryDialog;
    private InventoryItem editingItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        presenter = new InventoryPresenter(this);

        childSpinner = findViewById(R.id.spinnerChildList);
        inventoryContainer = findViewById(R.id.inventoryListContainer);

        Button addBtn = findViewById(R.id.btnAddItem);
        addBtn.setOnClickListener(v -> presenter.startAddNew());

        Button back = findViewById(R.id.btnBackToHome);
        back.setOnClickListener(v -> navigateToMainActivity());

        presenter.loadChildren();
    }

    // ============================================================
    // CHILD LIST
    // ============================================================
    public void displayChildren(List<String> names) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        childSpinner.setAdapter(adapter);
        childSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
                presenter.onChildSelected(i);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // ============================================================
    // INVENTORY LIST
    // ============================================================
    public void clearInventoryList() {
        inventoryContainer.removeAllViews();
    }

    public void displayNoInventoryMessage() {
        TextView tv = new TextView(this);
        tv.setText("No medications added.");
        tv.setTextSize(16);
        inventoryContainer.addView(tv);
    }

    public void addInventoryItemCard(InventoryItem item) {
        View card = getLayoutInflater().inflate(R.layout.item_inventory_card, null);

        TextView name = card.findViewById(R.id.tvMedicationName);
        TextView amount = card.findViewById(R.id.tvAmountLeft);
        TextView expiry = card.findViewById(R.id.tvExpiryDate);
        Button edit = card.findViewById(R.id.btnEditMedication);

        name.setText(item.medicationName);
        amount.setText("Left: " + item.amountLeft + " / " + item.totalAmount);
        expiry.setText("Expires: " + formatDate(item.expiryDate));

        edit.setOnClickListener(v -> presenter.startEdit(item));

        inventoryContainer.addView(card);
    }

    private String formatDate(long millis) {
        return new SimpleDateFormat("MMM d, yyyy").format(new Date(millis));
    }

    // ============================================================
    // POPUP
    // ============================================================
    public void showAddEditPopup(InventoryItem existingItem) {
        this.editingItem = existingItem;

        inventoryDialog = new Dialog(this);
        inventoryDialog.setContentView(R.layout.dialog_edit_inventory);

        TextView title = inventoryDialog.findViewById(R.id.tvDialogTitle);
        EditText name = inventoryDialog.findViewById(R.id.etMedicationName);
        EditText total = inventoryDialog.findViewById(R.id.etDosageTotalAmount);
        EditText left = inventoryDialog.findViewById(R.id.etDosageAmountLeft);
        EditText purchase = inventoryDialog.findViewById(R.id.etMedicinePurchaseDate);
        EditText expiry = inventoryDialog.findViewById(R.id.etMedExpiryDate);

        Button save = inventoryDialog.findViewById(R.id.btnSaveInventoryItemMed);
        Button delete = inventoryDialog.findViewById(R.id.btnDeleteInventoryItemMed);

        // === EDIT EXISTING ===
        if (existingItem != null) {
            title.setText("Edit Medication");

            name.setText(existingItem.medicationName);
            total.setText(String.valueOf(existingItem.totalAmount));
            left.setText(String.valueOf(existingItem.amountLeft));

            purchase.setText(formatFieldDate(existingItem.purchaseDate));
            expiry.setText(formatFieldDate(existingItem.expiryDate));

            delete.setVisibility(View.VISIBLE);
            delete.setOnClickListener(v -> presenter.deleteItem(existingItem));

        } else {
            title.setText("Add Medication");
            delete.setVisibility(View.GONE);
        }

        save.setOnClickListener(v -> presenter.saveItem());

        inventoryDialog.show();
    }

    private String formatFieldDate(long millis) {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date(millis));
    }

    public void closeInventoryPopup() {
        if (inventoryDialog != null) inventoryDialog.dismiss();
    }

    // ============================================================
    // GETTERS (MATCH XML)
    // ============================================================
    public String getMedicationName() {
        return getPopupText(R.id.etMedicationName);
    }

    public String getTotalAmount() {
        return getPopupText(R.id.etDosageTotalAmount);
    }

    public String getAmountLeft() {
        return getPopupText(R.id.etDosageAmountLeft);
    }

    public String getPurchaseDate() {
        return getPopupText(R.id.etMedicinePurchaseDate);
    }

    public String getExpiryDate() {
        return getPopupText(R.id.etMedExpiryDate);
    }

    private String getPopupText(int id) {
        EditText et = inventoryDialog.findViewById(id);
        return et.getText().toString().trim();
    }

    // ============================================================
    // NAV + FEEDBACK
    // ============================================================
    public void showSuccess(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void showError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void navigateToMainActivity() {
        startActivity(new Intent(this, MainActivityView.class));
        finish();
    }
}
