package com.example.cscb07_final_project_smartair.Users;

import java.util.HashMap;
import java.util.Map;
import com.example.cscb07_final_project_smartair.Users.ChildPermissions;

public class Child extends User{
    public String parent_id;
    public float pb_pef; //for pef
    public pefGuidance pef_guidance;
    public Map<String, ChildPermissions> sharingPerms; //for granular sharing
    public String notes;
    public long dob;

    public Child(){
    }

    public Child(String fullName, String uid, String email, String parent_id,
                 pefGuidance pef_guidance, long dob, String notes, Float pb_pef){
        super(fullName,uid,email,"Child");
        sharingPerms = new HashMap<>();
        this.pef_guidance = pef_guidance;
        this.parent_id = parent_id;
        this.dob = dob;
        this.notes = notes;
        this.pb_pef = pb_pef;
    }
}
