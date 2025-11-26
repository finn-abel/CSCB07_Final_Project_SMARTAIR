package com.example.cscb07_final_project_smartair.Models;

import com.example.cscb07_final_project_smartair.Repository.R3_Repository;
import com.example.cscb07_final_project_smartair.Repository.RepositoryCallback;
import com.example.cscb07_final_project_smartair.Models.Items.InventoryItem;

import java.util.List;

public class InventoryModel {
    private final R3_Repository repo;

    public InventoryModel(R3_Repository repo) {
        this.repo = repo;
    }

    public void getInventory(String childId, RepositoryCallback<List<InventoryItem>> cb) {
        repo.getInventory(childId, cb);
    }

    public void addItem(String childId, InventoryItem item, RepositoryCallback<Void> cb) {
        repo.addInventoryItem(childId, item, cb);
    }

    public void updateItem(String childId, InventoryItem item, RepositoryCallback<Void> cb) {
        repo.updateInventoryItem(childId, item, cb);
    }

    public void reduceItemFromUsage(String childId, String medName, int amountUsed, RepositoryCallback<Void> cb) {
        repo.updateInventoryAfterDose(childId, medName, amountUsed, cb);
    }
}

