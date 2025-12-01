package com.example.cscb07_final_project_smartair.Users;

public abstract class User {
    String name;
    String uid; //firebase id
    String email; //becomes username for children
    String role; // PARENT, CHILD, PROVIDER


    public User(){
    }

    public User(String name, String uid, String email, String role){
        this.name = name;
        this.uid = uid;
        this.email = email;
        this.role = role;
    }
}
