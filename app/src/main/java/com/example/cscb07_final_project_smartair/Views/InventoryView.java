package com.example.cscb07_final_project_smartair.Views;

import com.example.cscb07_final_project_smartair.DataObjects.InventoryItem;

import java.util.List;

public interface InventoryView {
    void displayChildren(List<String> childNames, List<String> childIds);

    void clearInventoryList();
    void displayNoInventoryMessage();
    void addInventoryItemCard(InventoryItem item);

    void showAddEditPopup(InventoryItem existingItem);
    void closeInventoryPopup();

    public String getMedicationName();
    public String getTotalAmount();
    public String getAmountLeft();
    public String getPurchaseDate();
    public String getExpiryDate();
    public String getMedicationType();

    void showSuccess(String msg);
    void showError(String msg);

    void navigateToMainActivity();
}
