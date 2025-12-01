package com.example.cscb07_final_project_smartair.DataObjects;

public class Badge {
    public String title;
    public String description;
    public int iconRes;   // drawable
    public long timestamp;

    public Badge() {
    }

    public Badge(String title, String description, int iconRes) {
        this.title = title;
        this.description = description;
        this.iconRes = iconRes;
        this.timestamp = System.currentTimeMillis();
    }
}
