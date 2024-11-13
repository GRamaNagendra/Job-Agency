package com.genuinecoder.learnspringsecurity.exception;

public class JobPostingNotFoundException extends RuntimeException {
    public JobPostingNotFoundException(String message) {
        super(message);
    }
}