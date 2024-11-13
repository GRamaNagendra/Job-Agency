package com.example.notification.controller;

import com.example.notification.service.NotificationService;
import org.springframework.web.bind.annotation.*;
import com.example.notification.service.NotificationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/notifications1")
@CrossOrigin(origins = "http://localhost:3000")  // Allow requests from the React app
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public String sendNotification(@RequestParam String targetToken, @RequestParam String title, @RequestParam String body) {
        notificationService.sendNotification(targetToken, title, body);
        return "Notification sent!";
    }
}
