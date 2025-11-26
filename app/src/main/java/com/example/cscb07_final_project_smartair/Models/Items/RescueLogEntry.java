package com.example.cscb07_final_project_smartair.Models.Items;

public class RescueLogEntry {
    public long timestamp;
    public int doseCount;
    public boolean betterAfter;

    public RescueLogEntry() {}

    public RescueLogEntry(long timestamp, int doseCount, boolean betterAfter) {
        this.timestamp = timestamp;
        this.doseCount = doseCount;
        this.betterAfter = betterAfter;
    }
}
