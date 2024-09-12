package com.genuinecoder.learnspringsecurity;

import com.genuinecoder.learnspringsecurity.model.MyUser;
import com.genuinecoder.learnspringsecurity.model.MyUserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;

public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final MyUserRepository myUserRepository;

    public OAuth2AuthenticationSuccessHandler(MyUserRepository myUserRepository) {
        this.myUserRepository = myUserRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        var oauth2User = (org.springframework.security.oauth2.core.user.OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");
        String username = oauth2User.getAttribute("name");
        String profilePicture = oauth2User.getAttribute("picture"); // Get profile picture from OAuth2 provider

        // Find user by email or create a new one if not found
        MyUser user = myUserRepository.findByEmail(email).orElseGet(() -> {
            MyUser newUser = new MyUser();
            newUser.setUsername(username);
            newUser.setPassword(""); // Password not used for OAuth2
            newUser.setEmail(email);
            newUser.setRole("ROLE_USER");
            newUser.setProfilePicture(profilePicture); // Save the profile picture URL
            newUser.setLastLogin(LocalDateTime.now()); // Set the initial login time
            return myUserRepository.save(newUser);
        });

        // Update existing user details (profile picture and last login)
        user.setLastLogin(LocalDateTime.now());
        user.setProfilePicture(profilePicture);
        myUserRepository.save(user);

        // Set new authentication token with the updated user details and role
        var authorities = Collections.singleton(new org.springframework.security.core.authority.SimpleGrantedAuthority(user.getRole()));
        Authentication newAuth = new UsernamePasswordAuthenticationToken(user, null, authorities);

        // Update security context with new authentication
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        // Redirect after successful login
        super.onAuthenticationSuccess(request, response, newAuth);
    }
}
