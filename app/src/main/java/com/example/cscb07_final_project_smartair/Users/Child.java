package com.example.cscb07_final_project_smartair.Users;

import java.util.HashMap;
import java.util.Map;

public class Child extends User{
    public String parent_id;
    public Float pb_pef; //for pef
    public pefGuidance pef_guidance;
    public Map<String, ChildPermissions> sharingPerms; //for granular sharing

    public Child(){
    }

    public Child(String fullName, String uid, String email, String parent_id,
                 pefGuidance pef_guidance){
        super(fullName,uid,email,"Child");
        this.sharingPerms = new HashMap<>();
        this.pef_guidance = pef_guidance;
    }
}
