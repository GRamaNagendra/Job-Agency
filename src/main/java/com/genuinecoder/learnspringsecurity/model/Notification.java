package com.genuinecoder.learnspringsecurity.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long jobId;  // This could be null for custom notifications
    private String notificationMessage;

    // Default Constructor
    public Notification() {}

    // Constructor with all fields
    public Notification(Long id, Long jobId, String notificationMessage) {
        this.id = id;
        this.jobId = jobId;
        this.notificationMessage = notificationMessage;
    }

    // Constructor with jobId only (useful for creating a notification linked to a specific job)
    public Notification(Long jobId, String notificationMessage) {
        this.jobId = jobId;
        this.notificationMessage = notificationMessage;
    }

    public Notification(Long jobId) {
        this.jobId = jobId;
       
    }
    
    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    
}
