package com.example.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    public void sendNotification(String targetToken, String title, String body) {
        Message message = Message.builder()
                .putData("title", title)
                .putData("body", body)
                .setToken(targetToken)
                .build();

        FirebaseMessaging.getInstance().sendAsync(message);
    }
}
