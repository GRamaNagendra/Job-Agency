package com.genuinecoder.learnspringsecurity.exception;

import java.nio.file.AccessDeniedException;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    (AccessDeniedException.class)
    public String handleAccessDeniedException(AccessDeniedException ex) {
        return "error/403"; // Ensure you have a 403.html page under /resources/templates/error/
    }
}
