package com.example.cscb07_final_project_smartair.Presenters;

//import com.example.smartair.contracts.InventoryContract;
import com.example.cscb07_final_project_smartair.Views.InventoryView;
import com.example.cscb07_final_project_smartair.Models.Items.InventoryItem;
import com.example.cscb07_final_project_smartair.Models.InventoryModel;
import com.example.cscb07_final_project_smartair.Repository.RepositoryCallback;

import java.util.List;

public class InventoryPresenter{
    private final InventoryView view;
    private final InventoryModel model;

    public InventoryPresenter(InventoryView view) {
        this.view = view;
        this.model = new InventoryModel();
    }

    public void onBackClicked() {
        view.navigateBack();
    }

    public void loadInventory(String childId) {
        model.getInventory(childId, new RepositoryCallback<List<InventoryItem>>() {
            @Override
            public void onSuccess(List<InventoryItem> items) {
                view.showInventory(items);
            }
            @Override
            public void onFailure(Exception e) {
                view.showError("Failed to load inventory");
            }
        });
    }

    public void addItem(String childId, InventoryItem item) {
        model.addItem(childId, item, new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                view.showSuccess("Item added!");
                loadInventory(childId);
            }
            @Override
            public void onFailure(Exception e) {
                view.showError("Failed to add item");
            }
        });
    }

    public void updateItem(String childId, InventoryItem item) {
        model.updateItem(childId, item, new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                view.showSuccess("Item updated!");
                loadInventory(childId);
            }
            @Override
            public void onFailure(Exception e) {
                view.showError("Failed to update item");
            }
        });
    }
}

