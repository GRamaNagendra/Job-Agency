package com.genuinecoder.learnspringsecurity.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactFormRepository extends JpaRepository<ContactForm, Long> {
}
