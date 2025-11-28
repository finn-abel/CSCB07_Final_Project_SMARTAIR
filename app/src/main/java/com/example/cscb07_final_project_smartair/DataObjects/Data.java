package com.example.cscb07_final_project_smartair.DataObjects;

public abstract class Data{
    public long timestamp;
    public String authorID;
    public String type;

    public Data() {};

    public Data(long timestamp, String authorID, String type){
        this.timestamp = timestamp;
        this.authorID = authorID;
        this.type = type;
    }
}
