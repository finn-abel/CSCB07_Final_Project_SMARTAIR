package com.example.cscb07_final_project_smartair.DataObjects;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class pefData extends Data{
    public Float pre;
    public Float post;
    public Float current;
    public float pb_pef;

    //use Float instead of float to allow for null


    public pefData(){
    }

    public pefData(Float pre, Float post, Float current, float pb_pef){
        super(System.currentTimeMillis(), FirebaseAuth.getInstance().getCurrentUser().getUid(),"PEF");
        this.pre = pre;
        this.post = post;
        this.current = current;
        this.pb_pef = pb_pef;
    }
}
