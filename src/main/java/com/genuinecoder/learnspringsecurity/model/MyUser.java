package com.genuinecoder.learnspringsecurity.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class MyUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String username;
    private String password;
    private String role; // Eg: ADMIN, USER
    private String email;
    private Long mobilenumber; 
    private LocalDateTime lastLogin; // Field for last login timestamp
    private String profilePicture;  // Field for profile picture URL

    // Constructor with all fields
    public MyUser(String username, String password, String role, String email, Long mobilenumber, LocalDateTime lastLogin, String profilePicture) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
        this.mobilenumber = mobilenumber;
        this.lastLogin = lastLogin;
        this.profilePicture = profilePicture;
    }

    // Default constructor
    public MyUser() {
        // Default constructor
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getMobilenumber() {
        return mobilenumber;
    }

    public void setMobilenumber(Long mobilenumber) {
        this.mobilenumber = mobilenumber;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
