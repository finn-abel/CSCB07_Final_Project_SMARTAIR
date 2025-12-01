package com.example.cscb07_final_project_smartair.Presenters;

import com.example.cscb07_final_project_smartair.DataObjects.InventoryItem;
import com.example.cscb07_final_project_smartair.Models.InventoryModel;
import com.example.cscb07_final_project_smartair.Views.InventoryActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class InventoryPresenter {
    private final InventoryActivity view;
    private final InventoryModel model;

    private List<String> childIds;
    private String selectedChildId;

    private InventoryItem editingItem = null;

    public InventoryPresenter(InventoryActivity view) {
        this.view = view;
        this.model = new InventoryModel(this);
    }

    public void loadChildren() {
        model.getChildren();
    }

    public void onChildrenLoaded(List<String> names, List<String> ids) {
        childIds = ids;
        view.displayChildren(names, ids);
    }

    // Called when child selection fails
    public void onFailure(String msg) {
        view.showError(msg);
    }

    public void onChildSelected(int index) {
        if (index < 0 || index >= childIds.size()) {
            view.showError("Invalid child selection.");
            return;
        }
        selectedChildId = childIds.get(index);
        loadInventory();
    }

    public void loadInventory() {
        if (selectedChildId == null) {
            view.showError("No child selected.");
            return;
        }
        model.getInventory(selectedChildId);
    }

    public void onInventoryLoaded(List<InventoryItem> items) {
        view.clearInventoryList();

        if (items.isEmpty()) {
            view.displayNoInventoryMessage();
            return;
        }

        for (InventoryItem item : items) {
            view.addInventoryItemCard(item);
        }
    }

    public void startAddNew() {
        editingItem = null;
        view.showAddEditPopup(null);
    }

    public void startEdit(InventoryItem item) {
        editingItem = item;
        view.showAddEditPopup(item);
    }

    public void saveItem() {
        String name = view.getMedicationName();
        String totalStr = view.getTotalAmount();
        String leftStr = view.getAmountLeft();
        String purchaseStr = view.getPurchaseDate();
        String expiryStr = view.getExpiryDate();

        if (name.isEmpty() || totalStr.isEmpty() || leftStr.isEmpty()
                || purchaseStr.isEmpty() || expiryStr.isEmpty()) {
            view.showError("All fields are required");
            return;
        }

        int total = Integer.parseInt(totalStr);
        int left = Integer.parseInt(leftStr);

        long purchaseDate;
        long expiryDate;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            purchaseDate = sdf.parse(purchaseStr).getTime();
            expiryDate = sdf.parse(expiryStr).getTime();
        } catch (ParseException e) {
            view.showError("Date format must be YYYY-MM-DD");
            return;
        }
        String medType = view.getMedicationType();

        InventoryItem item = new InventoryItem(selectedChildId, name, total, left, purchaseDate,
                expiryDate, medType);
        model.saveItem(selectedChildId, item);
    }


    public void onSaveSuccess() {
        view.showSuccess("Saved!");
        view.closeInventoryPopup();
        loadInventory();
    }

    public void deleteItem(InventoryItem editingItem) {
        if (editingItem == null) return;
        model.deleteItem(selectedChildId, editingItem.medicationName);
    }

    public void onDeleteSuccess() {
        view.showSuccess("Deleted.");
        view.closeInventoryPopup();
        loadInventory();
    }
}
