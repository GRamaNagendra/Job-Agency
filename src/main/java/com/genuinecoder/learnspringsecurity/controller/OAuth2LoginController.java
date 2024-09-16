package com.genuinecoder.learnspringsecurity.controller;

import com.genuinecoder.learnspringsecurity.model.MyUser;
import com.genuinecoder.learnspringsecurity.model.MyUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/oauth2")
public class OAuth2LoginController {

    @Autowired
    private MyUserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> loginWithGoogle(@RequestBody String token) {
        // Handle Google token validation (this can be done using Google's TokenInfo API or other means)
        // Fetch user info based on token

        // For example:
        String email = "user@example.com"; // Retrieved from the token validation
        String name = "User Name";
        String profilePicture = "https://example.com/profile.jpg"; // URL from the token response

        // Find or create the user in the database
        Optional<MyUser> userOptional = userRepository.findByEmail(email);
        MyUser user = userOptional.orElseGet(() -> {
            MyUser newUser = new MyUser();
            newUser.setEmail(email);
            newUser.setUsername(name);
            newUser.setProfilePicture(profilePicture);
            newUser.setRole("ROLE_USER");
            newUser.setLastLogin(LocalDateTime.now());
            return userRepository.save(newUser);
        });

        // Update user login time
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // Return a success response with JWT or session info
        // You can integrate JWT here and return a token for further requests
        return ResponseEntity.ok().body("Login successful!");
    }
    
    
}
