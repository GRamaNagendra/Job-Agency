package com.genuinecoder.learnspringsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.genuinecoder.learnspringsecurity.model.Feedback;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByUserId(Long userId);
}
