package com.example.cscb07_final_project_smartair.Users;

import java.util.HashMap;
import java.util.Map;

public class Child extends User{
    String parent_id;
    int pb_pef; //for pef
    String [] pef_instructions;

    Map<String, ChildPermissions> sharingPerms; //for granular sharing

    public Child(){
    }

    public Child(String fullName, String uid, String email, String parent_id){
        super(fullName,uid,email,"Child");
        this.sharingPerms = new HashMap<>();
    }
}
