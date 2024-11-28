package com.genuinecoder.learnspringsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.genuinecoder.learnspringsecurity.model.Notification;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
	 List<Notification> findByJobId(Long jobId);
	


	    // Delete all notifications related to a specific job ID
	    void deleteByJobId(Long jobId);
}
