package com.example.cscb07_final_project_smartair.Views;

import com.example.cscb07_final_project_smartair.DataObjects.InventoryItem;

import java.util.List;

public interface InventoryView {

    // CHILD HANDLING
    void displayChildren(List<String> childNames, List<String> childIds);
    void showChildSelectionError(String msg);

    // INVENTORY DISPLAY
    void clearInventoryList();
    void displayNoInventoryMessage();
    void addInventoryItemCard(InventoryItem item);

    // POPUP HANDLING
    void showAddEditPopup(InventoryItem existingItem);
    void closeInventoryPopup();

    // GETTERS FROM POPUP
    String getMedicationName();
    String getTotalAmount();
    String getAmountLeft();
    String getPurchaseDate();
    String getExpiryDate();

    // FEEDBACK
    void showSuccess(String msg);
    void showError(String msg);

    // NAVIGATION
    void navigateToMainActivity();
}
