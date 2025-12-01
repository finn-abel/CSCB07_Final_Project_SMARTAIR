package com.example.cscb07_final_project_smartair.DataObjects;

public class pefCapture {
    public String childID;
    public Float pre;
    public Float post;
    public Float current;

    public pefCapture(String childID, Float pre, Float post, Float current){
        this.childID = childID;
        this.pre = pre;
        this.post = post;
        this.current = current;
    }
}
