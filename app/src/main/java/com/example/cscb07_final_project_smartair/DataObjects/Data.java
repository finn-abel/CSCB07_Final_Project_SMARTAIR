package com.example.cscb07_final_project_smartair.DataObjects;

public abstract class Data{
    long timestamp;
    String authorID;
    String type;

    public Data() {};

    public Data(long timestamp, String authorID, String type){
        this.timestamp = timestamp;
        this.authorID = authorID;
        this.type = type;
    }
}
