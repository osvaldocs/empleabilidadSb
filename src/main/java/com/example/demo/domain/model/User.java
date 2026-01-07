package com.example.demo.domain.model;

import java.util.UUID;

public class User {
    private UUID id;
    private String username;
    private String password;

    public User(UUID id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }


    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    // getters
    public UUID getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}
