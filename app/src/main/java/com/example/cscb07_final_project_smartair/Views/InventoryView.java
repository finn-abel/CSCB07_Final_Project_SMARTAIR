package com.example.cscb07_final_project_smartair.Views;

import com.example.cscb07_final_project_smartair.Models.Items.InventoryItem;

import java.util.List;

public interface InventoryView {
    void showInventory(List<InventoryItem> items);
    void showSuccess(String msg);
    void showError(String msg);
    void navigateBack();
}
