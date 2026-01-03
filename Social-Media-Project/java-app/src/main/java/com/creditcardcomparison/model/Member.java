package com.creditcardcomparison.model;

public class Member {

    private String username;

    private int id;


    // Constructor
    public Member() {
    }


    // Getters
    public String getUsername() {
        return username;
    }

    public int getId() { return this.id; }


    // Setters
    public void setUsername(String username) {
        this.username = username;
    }

    public void setId(int id) { this.id = id; }
}
