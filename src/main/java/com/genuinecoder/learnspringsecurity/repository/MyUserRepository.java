package com.genuinecoder.learnspringsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.genuinecoder.learnspringsecurity.model.MyUser;

import java.util.Optional;

public interface MyUserRepository extends JpaRepository<MyUser, Long> {
    Optional<MyUser> findByUsername(String username);

    Optional<MyUser> findByEmail(String email);
}
