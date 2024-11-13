package com.example.notification.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp initializeFirebase() throws IOException {
        // Load the service account key from the classpath
        InputStream serviceAccount = null;
        try {
            serviceAccount = new ClassPathResource("serviceAccountKey.json").getInputStream();

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            // Initialize Firebase
            return FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            // Log the exception or rethrow with a more informative message
            throw new IOException("Failed to initialize Firebase: " + e.getMessage(), e);
        } finally {
            // Ensure the input stream is closed
            if (serviceAccount != null) {
                try {
                    serviceAccount.close();
                } catch (IOException ex) {
                    // Log the exception if closing the stream fails
                    System.err.println("Failed to close service account input stream: " + ex.getMessage());
                }
            }
        }
    }
}
