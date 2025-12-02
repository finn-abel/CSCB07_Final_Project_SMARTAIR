package com.example.cscb07_final_project_smartair.Users;

import java.util.HashMap;
import java.util.Map;
import com.example.cscb07_final_project_smartair.Users.ChildPermissions;

public class Child extends User{
    public String parent_id;
    public float pb_pef; //for pef
    public pefGuidance pef_guidance;
    public Map<String, ChildPermissions> sharingPerms; //for granular sharing

    public Child(){
    }

    public Child(String fullName, String uid, String email, String parent_id,
                 pefGuidance pef_guidance){
        super(fullName,uid,email,"Child");
        sharingPerms = new HashMap<>();
        this.pef_guidance = pef_guidance;

        sharingPerms.put("Provider1", new ChildPermissions()); //TESTING
    }
}
