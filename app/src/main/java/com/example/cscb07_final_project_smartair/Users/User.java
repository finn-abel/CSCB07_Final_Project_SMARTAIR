package com.example.cscb07_final_project_smartair.Users;

public abstract class User {
    String fullName;
    String uid; //firebase id
    String email; //becomes username for children
    String role; // PARENT, CHILD, PROVIDER


    public User(){
    }

    public User(String fullName, String uid, String email, String role){
        this.fullName = fullName;
        this.uid = uid;
        this.email = email;
        this.role = role;
    }
}
