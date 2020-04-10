package com.example.envirometalist.model;

public class UserEntity {
    private String email;
    private UserRoleEntity role;
    private String userName;
    private String avatar;

    public UserEntity(String email, UserRoleEntity role, String userName, String avatar) {
        this.email = email;
        this.role = role;
        this.userName = userName;
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public UserRoleEntity getRole() {
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
