package com.example.cscb07_final_project_smartair.Users;

public class ChildSpinnerOption {
    public String userID;
    public String fullName;

    public ChildSpinnerOption(String userID, String fullName){
        this.userID = userID;
        this.fullName = fullName;
    }

    @Override
    public String toString(){
        return this.fullName;
    }
}
