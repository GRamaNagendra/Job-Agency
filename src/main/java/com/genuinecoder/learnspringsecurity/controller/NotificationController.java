package com.genuinecoder.learnspringsecurity.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.genuinecoder.learnspringsecurity.model.Notification;
import com.genuinecoder.learnspringsecurity.service.NotificationService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/interaction")  // Base path for the interaction controller
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // Create a new notification message
    @PostMapping("/create")  // Endpoint to create a new notification
    public ResponseEntity<Notification> createNotification(@RequestBody Notification notification) {
        try {
            Notification savedNotification = notificationService.createNotification(notification);
            return new ResponseEntity<>(savedNotification, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get all notifications
    @GetMapping("/all")  // Endpoint to get all notifications
    public ResponseEntity<List<Notification>> getAllNotifications() {
        try {
            List<Notification> notifications = notificationService.getAllNotifications();
            if (notifications.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 if no content
            }
            return new ResponseEntity<>(notifications, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get a notification by ID
    @GetMapping("/{id}")  // Endpoint to get a notification by its ID
    public ResponseEntity<Notification> getNotificationById(@PathVariable Long id) {
        try {
            Optional<Notification> notification = notificationService.getNotificationById(id);
            return notification.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build()); // 404 if not found
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Update an existing notification message
    @PutMapping("/update/{id}")  // Endpoint to update an existing notification by ID
    public ResponseEntity<Notification> updateNotification(@PathVariable Long id, @RequestBody Notification notification) {
        try {
            Notification updatedNotification = notificationService.updateNotification(id, notification);
            return updatedNotification != null
                    ? new ResponseEntity<>(updatedNotification, HttpStatus.OK) // 200 if updated
                    : new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 if not found
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 for server errors
        }
    }

    // Delete a notification message
    @DeleteMapping("/delete/{id}")  // Endpoint to delete a notification by ID
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        try {
            boolean deleted = notificationService.deleteNotification(id);
            return deleted
                    ? ResponseEntity.noContent().build() // 204 if deleted successfully
                    : ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 if not found
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 for server errors
        }
    }
}
