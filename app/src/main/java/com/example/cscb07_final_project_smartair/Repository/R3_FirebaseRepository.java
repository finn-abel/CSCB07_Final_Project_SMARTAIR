package com.example.cscb07_final_project_smartair.Repository;

import androidx.annotation.NonNull;

import com.example.cscb07_final_project_smartair.Models.Items.RescueLogEntry;
import com.example.cscb07_final_project_smartair.Models.Items.ControllerLogEntry;
import com.example.cscb07_final_project_smartair.Models.Items.InventoryItem;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class R3_FirebaseRepository implements R3_Repository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // ============================================================
    //                         RESCUE LOGS
    // ============================================================

    @Override
    public void addRescueLog(String childId,
                             RescueLogEntry entry,
                             RepositoryCallback<Void> cb) {

        db.collection("children")
                .document(childId)
                .collection("logs")
                .document("rescue")
                .collection("entries")
                .add(entry)
                .addOnSuccessListener(doc -> cb.onSuccess(null))
                .addOnFailureListener(cb::onFailure);
    }

    @Override
    public void getRescueLogs(String childId,
                              RepositoryCallback<List<RescueLogEntry>> cb) {

        db.collection("children")
                .document(childId)
                .collection("logs")
                .document("rescue")
                .collection("entries")
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<RescueLogEntry> list = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        RescueLogEntry entry = doc.toObject(RescueLogEntry.class);
                        list.add(entry);
                    }
                    cb.onSuccess(list);
                })
                .addOnFailureListener(cb::onFailure);
    }

    // ============================================================
    //                      CONTROLLER LOGS
    // ============================================================

    @Override
    public void addControllerLog(String childId,
                                 ControllerLogEntry entry,
                                 RepositoryCallback<Void> cb) {

        db.collection("children")
                .document(childId)
                .collection("logs")
                .document("controller")
                .collection("entries")
                .add(entry)
                .addOnSuccessListener(doc -> cb.onSuccess(null))
                .addOnFailureListener(cb::onFailure);
    }

    @Override
    public void getControllerLogs(String childId,
                                  RepositoryCallback<List<ControllerLogEntry>> cb) {

        db.collection("children")
                .document(childId)
                .collection("logs")
                .document("controller")
                .collection("entries")
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<ControllerLogEntry> list = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        ControllerLogEntry entry = doc.toObject(ControllerLogEntry.class);
                        list.add(entry);
                    }
                    cb.onSuccess(list);
                })
                .addOnFailureListener(cb::onFailure);
    }

    // ============================================================
    //                          INVENTORY
    // ============================================================

    @Override
    public void getInventory(String childId,
                             RepositoryCallback<List<InventoryItem>> cb) {

        db.collection("children")
                .document(childId)
                .collection("inventory")
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<InventoryItem> list = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        InventoryItem item = doc.toObject(InventoryItem.class);
                        list.add(item);
                    }
                    cb.onSuccess(list);
                })
                .addOnFailureListener(cb::onFailure);
    }

    @Override
    public void addInventoryItem(String childId,
                                 InventoryItem item,
                                 RepositoryCallback<Void> cb) {

        db.collection("children")
                .document(childId)
                .collection("inventory")
                .document(item.name) // use medication name as key
                .set(item)
                .addOnSuccessListener(unused -> cb.onSuccess(null))
                .addOnFailureListener(cb::onFailure);
    }

    @Override
    public void updateInventoryItem(String childId,
                                    InventoryItem item,
                                    RepositoryCallback<Void> cb) {

        db.collection("children")
                .document(childId)
                .collection("inventory")
                .document(item.name)
                .set(item)
                .addOnSuccessListener(unused -> cb.onSuccess(null))
                .addOnFailureListener(cb::onFailure);
    }

    // ============================================================
    //        INVENTORY AUTO-UPDATE AFTER MEDICINE USAGE
    // ============================================================

    @Override
    public void updateInventoryAfterDose(String childId,
                                         String medicationName,
                                         int amountUsed,
                                         RepositoryCallback<Void> cb) {

        db.collection("children")
                .document(childId)
                .collection("inventory")
                .document(medicationName)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.exists()) {
                        cb.onFailure(new Exception("Medication not in inventory"));
                        return;
                    }

                    InventoryItem item = snapshot.toObject(InventoryItem.class);
                    if (item == null) {
                        cb.onFailure(new Exception("Invalid inventory item"));
                        return;
                    }

                    int newAmount = Math.max(item.amountLeft - amountUsed, 0);
                    item.amountLeft = newAmount;

                    snapshot.getReference()
                            .set(item)
                            .addOnSuccessListener(unused -> cb.onSuccess(null))
                            .addOnFailureListener(cb::onFailure);

                })
                .addOnFailureListener(cb::onFailure);
    }
}
