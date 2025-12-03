package com.example.cscb07_final_project_smartair.DataObjects;

import com.google.firebase.auth.FirebaseAuth;

public class InventoryItem extends Data {
    public String medicationName;
    public int totalAmount;
    public int amountLeft;
    public long purchaseDate;
    public long expiryDate;
    public String medType;

    public String childId;

    public InventoryItem() {
        //empty for FB
    }

    public InventoryItem(String childId, String medicationName, int totalAmount, int amountLeft, long purchaseDate, long expiryDate, String medType) {
        super(System.currentTimeMillis(), FirebaseAuth.getInstance().getCurrentUser().getUid(), "inventoryItem");
        this.childId = childId;
        this.medicationName = medicationName;
        this.totalAmount = totalAmount;
        this.amountLeft = amountLeft;
        this.purchaseDate = purchaseDate;
        this.expiryDate = expiryDate;
        this.medType = medType;
    }
}
