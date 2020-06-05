package com.example.envirometalist.model;

import java.io.Serializable;

public class User implements Serializable {

    private String email;
    private UserRole role;
    private String username;
    private String avatar;

    public User(){

    }

    public User(String email, UserRole role, String username, String avatar) {
        this.email = email;
        this.role = role;
        this.username = username;
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public UserRole getRole() {
        return role;
    }

    public String getUsername() {
        return username;
    }

    public String getAvatar() {
        return avatar;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "email='" + email + '\'' +
                ", role=" + role +
                ", userName='" + username + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}
