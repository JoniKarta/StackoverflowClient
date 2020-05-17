package com.example.envirometalist.model;

public class User {
    private String email;
    private UserRole role;
    private String userName;
    private String avatar;

    public User(String email, UserRole role, String userName, String avatar) {
        this.email = email;
        this.role = role;
        this.userName = userName;
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public UserRole getRole() {
        return role;
    }

    public String getUserName() {
        return userName;
    }

    public String getAvatar() {
        return avatar;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "email='" + email + '\'' +
                ", role=" + role +
                ", userName='" + userName + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}
