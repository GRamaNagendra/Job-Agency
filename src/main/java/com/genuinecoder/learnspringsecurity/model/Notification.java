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

    private String jobTitle;  // This could be null for non-job notifications
    private Long jobId;  // This could be null for custom notifications
    private Double salary;  // This could be null for non-job notifications
    private String message;  // Custom message or wish
    private String type;  // To differentiate between job, wish, or custom notifications
    private Long userId;  // To send notifications to specific users
	
    
	public Notification(String jobTitle, Long jobId, Double salary) {
		super();
		this.jobTitle = jobTitle;
		this.jobId = jobId;
		this.salary = salary;
	}
	public Notification() {
		super();
	}
	public Notification(Long id, String jobTitle, Long jobId, Double salary, String message, String type, Long userId) {
		super();
		this.id = id;
		this.jobTitle = jobTitle;
		this.jobId = jobId;
		this.salary = salary;
		this.message = message;
		this.type = type;
		this.userId = userId;
	}
	
	public Notification(String title, Long id2, double salary2) {
		super();
	
		this.jobTitle = title;
		this.jobId = id2;
		this.salary= salary;
		
		
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getJobTitle() {
		return jobTitle;
	}
	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}
	public Long getJobId() {
		return jobId;
	}
	public void setJobId(Long jobId) {
		this.jobId = jobId;
	}
	public Double getSalary() {
		return salary;
	}
	public void setSalary(Double salary) {
		this.salary = salary;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}

    // Constructors, Getters and Setters


}

