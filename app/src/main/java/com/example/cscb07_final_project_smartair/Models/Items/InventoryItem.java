package com.example.cscb07_final_project_smartair.Models.Items;

public class InventoryItem {
    public String name;
    public long purchaseDate;
    public long expiryDate;
    public int totalAmount;
    public int amountLeft;

    public InventoryItem() {}

    public InventoryItem(String name, long purchaseDate, long expiryDate, int totalAmount, int amountLeft) {
        this.name = name;
        this.purchaseDate = purchaseDate;
        this.expiryDate = expiryDate;
        this.totalAmount = totalAmount;
        this.amountLeft = amountLeft;
    }
}
