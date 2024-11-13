package com.genuinecoder.learnspringsecurity.controller;

import com.genuinecoder.learnspringsecurity.model.Notification;
import com.genuinecoder.learnspringsecurity.repository.NotificationRepository;
import com.genuinecoder.learnspringsecurity.util.ResponseStructure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/notifications")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    // Create a notification
    @PostMapping
    public ResponseEntity<ResponseStructure<Notification>> createNotification(@RequestBody Notification notification) {
        Notification savedNotification = notificationRepository.save(notification);

        ResponseStructure<Notification> responseStructure = new ResponseStructure<>();
        responseStructure.setStatus(HttpStatus.CREATED.value());
        responseStructure.setMessage("Notification Created Successfully");
        responseStructure.setData(savedNotification);

        return new ResponseEntity<>(responseStructure, HttpStatus.CREATED);
    }

    // Get all notifications
    @GetMapping
    public ResponseEntity<ResponseStructure<List<Notification>>> getAllNotifications() {
        List<Notification> notifications = notificationRepository.findAll();
        ResponseStructure<List<Notification>> responseStructure = new ResponseStructure<>();
        responseStructure.setStatus(HttpStatus.OK.value());
        responseStructure.setMessage("Notifications Retrieved Successfully");
        responseStructure.setData(notifications);

        return ResponseEntity.ok(responseStructure);
    }

    // Delete a notification by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseStructure<String>> deleteNotification(@PathVariable Long id) {
        return notificationRepository.findById(id)
                .map(existingNotification -> {
                    notificationRepository.delete(existingNotification);
                    ResponseStructure<String> responseStructure = new ResponseStructure<>();
                    responseStructure.setStatus(HttpStatus.NO_CONTENT.value());
                    responseStructure.setMessage("Notification Deleted Successfully");
                    responseStructure.setData("Deleted Notification with id " + id);
                    return ResponseEntity.ok(responseStructure);
                })
                .orElseThrow(() -> new RuntimeException("Notification not found with id " + id));
    }
    
    @PostMapping("/custom")
    @PreAuthorize("hasRole('ADMIN')")  // Only admin can send custom notifications
    public ResponseEntity<ResponseStructure<Notification>> sendCustomNotification(
            @RequestParam Long userId,
            @RequestParam String message,
            @RequestParam String type) {

        // Create a new notification
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setType(type);  // "wish" or "custom_message" for example

        Notification savedNotification = notificationRepository.save(notification);

        ResponseStructure<Notification> responseStructure = new ResponseStructure<>();
        responseStructure.setStatus(HttpStatus.CREATED.value());
        responseStructure.setMessage("Custom Notification Sent Successfully");
        responseStructure.setData(savedNotification);

        return new ResponseEntity<>(responseStructure, HttpStatus.CREATED);
    }

}
