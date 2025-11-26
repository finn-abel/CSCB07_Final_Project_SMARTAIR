package com.example.cscb07_final_project_smartair.Models;

import androidx.annotation.NonNull;

import com.example.cscb07_final_project_smartair.Models.Items.ControllerLogEntry;
import com.example.cscb07_final_project_smartair.Models.Items.RescueLogEntry;
import com.example.cscb07_final_project_smartair.Repository.RepositoryCallback;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MedicineLogsModel {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public MedicineLogsModel() {
        // Empty constructor
    }

    public void addRescueLog(String childId, @NonNull RescueLogEntry entry, RepositoryCallback<Void> cb) {
        db.collection("children")
                .document(childId)
                .collection("logs")
                .document("rescue")
                .collection("entries")
                .add(entry)
                .addOnSuccessListener(doc -> cb.onSuccess(null))
                .addOnFailureListener(cb::onFailure);
    }

    public void getRescueLogs(String childId, RepositoryCallback<List<RescueLogEntry>> cb) {
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

    public void addControllerLog(String childId, @NonNull ControllerLogEntry entry, RepositoryCallback<Void> cb) {
        db.collection("children")
                .document(childId)
                .collection("logs")
                .document("controller")
                .collection("entries")
                .add(entry)
                .addOnSuccessListener(doc -> cb.onSuccess(null))
                .addOnFailureListener(cb::onFailure);
    }

    public void getControllerLogs(String childId, RepositoryCallback<List<ControllerLogEntry>> cb) {
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
}
