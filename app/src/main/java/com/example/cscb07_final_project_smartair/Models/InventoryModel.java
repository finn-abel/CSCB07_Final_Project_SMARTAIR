package com.example.cscb07_final_project_smartair.Models;

import androidx.annotation.NonNull;

import com.example.cscb07_final_project_smartair.Models.Items.InventoryItem;
import com.example.cscb07_final_project_smartair.Repository.RepositoryCallback;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class InventoryModel {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public InventoryModel() {
        // Empty constructor
    }
    public void getInventory(String childId, RepositoryCallback<List<InventoryItem>> cb) {
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
    public void addItem(String childId, @NonNull InventoryItem item, RepositoryCallback<Void> cb) {
        db.collection("children")
                .document(childId)
                .collection("inventory")
                .document(item.name)  // use name as key
                .set(item)
                .addOnSuccessListener(unused -> cb.onSuccess(null))
                .addOnFailureListener(cb::onFailure);
    }
    public void updateItem(String childId, @NonNull InventoryItem item, RepositoryCallback<Void> cb) {
        db.collection("children")
                .document(childId)
                .collection("inventory")
                .document(item.name)
                .set(item)
                .addOnSuccessListener(unused -> cb.onSuccess(null))
                .addOnFailureListener(cb::onFailure);
    }

    public void updateInventoryAfterDose(String childId, String medName, int amountUsed, RepositoryCallback<Void> cb) {
        db.collection("children")
                .document(childId)
                .collection("inventory")
                .document(medName)
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
                    item.amountLeft = Math.max(item.amountLeft - amountUsed, 0);

                    snapshot.getReference()
                            .set(item)
                            .addOnSuccessListener(unused -> cb.onSuccess(null))
                            .addOnFailureListener(cb::onFailure);

                })
                .addOnFailureListener(cb::onFailure);
    }
}
