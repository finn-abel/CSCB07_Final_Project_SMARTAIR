package com.example.cscb07_final_project_smartair.Models;

public class TriageModel {

    private boolean speak;
    private boolean lips;
    private boolean chest;
    private String id;

    public TriageModel(String id, boolean speak, boolean lips, boolean chest){
        this.id=id;
        this.speak=speak;
        this.lips=lips;
        this.chest=chest;
    }// constructor

    public boolean isRedFlag(){
        return this.speak||this.lips||this.chest;
    }//check if any red flags are found

    public void logIncident(){
        //to be implemented
    }

}
