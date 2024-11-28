package com.genuinecoder.learnspringsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.genuinecoder.learnspringsecurity.model.ContactForm;

public interface ContactFormRepository extends JpaRepository<ContactForm, Long> {
}
