package com.example.cscb07_final_project_smartair.Models.Items;

public class ControllerLogEntry {
    public long timestamp;
    public int doseCount;

    public ControllerLogEntry() {}

    public ControllerLogEntry(long timestamp, int doseCount) {
        this.timestamp = timestamp;
        this.doseCount = doseCount;
    }
}
