package com.example.cscb07_final_project_smartair.DataObjects;

public class triageCapture {
    public boolean speak;
    public boolean lips;
    public boolean chest;
    public Float pef;
    public String userID;
    public boolean shared_rescue;


    public triageCapture(boolean speak, boolean lips, boolean chest,
                         Float pef, String userID, boolean shared_rescue){
        this.speak = speak;
        this.lips = lips;
        this.chest = chest;
        this.pef = pef;
        this.userID = userID;
        this.shared_rescue = shared_rescue;

    }

}
