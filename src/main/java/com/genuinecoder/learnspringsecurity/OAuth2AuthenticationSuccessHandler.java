package com.genuinecoder.learnspringsecurity;

import com.genuinecoder.learnspringsecurity.model.MyUser;
import com.genuinecoder.learnspringsecurity.model.MyUserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.core.Authentication;


import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;


import java.io.IOException;
import java.util.Collections;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;


import java.io.IOException;
import java.util.Collections;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;


import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

public class OAuth2AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final MyUserRepository myUserRepository;

    public OAuth2AuthenticationSuccessHandler(MyUserRepository myUserRepository) {
        this.myUserRepository = myUserRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        var oauth2User = (org.springframework.security.oauth2.core.user.OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");
        String username = oauth2User.getAttribute("name");
        String profilePicture = oauth2User.getAttribute("picture");

        // Find or create user
        MyUser user = myUserRepository.findByEmail(email).orElseGet(() -> {
            MyUser newUser = new MyUser();
            newUser.setUsername(username);
            newUser.setPassword(""); // No password for OAuth2
            newUser.setEmail(email);
            newUser.setRole("ROLE_USER");
            newUser.setProfilePicture(profilePicture);
            return myUserRepository.save(newUser);
        });

        // Update existing user details
        user.setLastLogin(LocalDateTime.now());
        user.setProfilePicture(profilePicture);
        myUserRepository.save(user);

        // Update authentication
        var authorities = Collections.singleton(new org.springframework.security.core.authority.SimpleGrantedAuthority(user.getRole()));
        Authentication newAuth = new UsernamePasswordAuthenticationToken(user, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        // Retrieve the original URL from cookies
        String redirectUri = null;
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals("originalUrl")) {
                redirectUri = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
                break;
            }
        }
        if (redirectUri == null || redirectUri.isEmpty()) {
            redirectUri = "http://localhost:3000"; // Default redirect URI
        }

        // Print the URL for debugging
        System.out.println("Redirecting to: " + redirectUri);

        // Clear the cookie
        Cookie clearCookie = new Cookie("originalUrl", null);
        clearCookie.setMaxAge(0);
        response.addCookie(clearCookie);

        // Redirect to the original URL
        response.sendRedirect(redirectUri);
    }
}