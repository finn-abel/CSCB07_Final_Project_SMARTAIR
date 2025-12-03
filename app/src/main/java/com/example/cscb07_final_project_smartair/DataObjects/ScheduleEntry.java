package com.example.cscb07_final_project_smartair.DataObjects;

public class ScheduleEntry {
    public String time;
    public int doseAmount;
    public String note;

    public ScheduleEntry() {
        //empty for FB
    }

    public ScheduleEntry(String time, int doseAmount, String note) {
        this.time = time;
        this.doseAmount = doseAmount;
        this.note = note;
    }
}