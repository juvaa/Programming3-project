package com.server;

public class User {
    private String username;
    private String password;
    private String salt;
    private String email;

    public User(String username, String password, String salt, String email) {
        this.username = username;
        this.password = password;
        this.salt = salt;
        this.email = email;
    }

    public User() {
        
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getSalt() {
        return salt;
    }

    public String getEmail() {
        return email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
