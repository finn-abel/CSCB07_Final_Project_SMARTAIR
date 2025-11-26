package com.example.cscb07_final_project_smartair.Repository;

import com.example.cscb07_final_project_smartair.Models.Items.RescueLogEntry;
import com.example.cscb07_final_project_smartair.Models.Items.ControllerLogEntry;
import com.example.cscb07_final_project_smartair.Models.Items.InventoryItem;

import java.util.List;

public interface R3_Repository {

    // RESCUE LOGS
    void addRescueLog(String childId, RescueLogEntry entry, RepositoryCallback<Void> cb);
    void getRescueLogs(String childId, RepositoryCallback<List<RescueLogEntry>> cb);

    // CONTROLLER LOGS
    void addControllerLog(String childId, ControllerLogEntry entry, RepositoryCallback<Void> cb);
    void getControllerLogs(String childId, RepositoryCallback<List<ControllerLogEntry>> cb);

    // INVENTORY
    void getInventory(String childId, RepositoryCallback<List<InventoryItem>> cb);
    void updateInventoryAfterDose(String childId, String medicationName, int amountUsed, RepositoryCallback<Void> cb);
    void addInventoryItem(String childId, InventoryItem item, RepositoryCallback<Void> cb);
    void updateInventoryItem(String childId, InventoryItem item, RepositoryCallback<Void> cb);
}
