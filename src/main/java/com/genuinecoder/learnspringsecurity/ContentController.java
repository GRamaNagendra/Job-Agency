package com.genuinecoder.learnspringsecurity;

import com.genuinecoder.learnspringsecurity.model.MyUser;
import com.genuinecoder.learnspringsecurity.model.MyUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping
public class ContentController {

    @Autowired
    private MyUserRepository myUserRepository;

    @GetMapping("/home")
    public String handleWelcome() {
        return "home";
    }
    
  

    @GetMapping("/admin/home")
    @PreAuthorize("hasRole('ADMIN')")
    public String handleAdminHome() {
        return "home_admin";
    }

    @GetMapping("/user/home")
    @PreAuthorize("hasRole('USER')")
    public String handleUserHome() {
        return "home_user";
    }

    @GetMapping("/login")
    public String handleLogin() {
        return "custom_login";
    }

    @GetMapping("/profile")
    @ResponseBody
    public Map<String, Object> profile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        String email;
        String username;
        String profilePicture = null;

        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
            email = token.getPrincipal().getAttribute("email");
            username = token.getPrincipal().getAttribute("name");
            profilePicture = token.getPrincipal().getAttribute("picture");  // Get Gmail profile picture URL
            
        } else if (authentication instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
            MyUser user = (MyUser) token.getPrincipal();
            email = user.getEmail();
            username = user.getUsername();
            profilePicture = user.getProfilePicture();  // Get profile picture from MyUser entity
            
        } else {
            throw new RuntimeException("Unsupported authentication type");
        }

        MyUser user = myUserRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("name", user.getUsername());
        userProfile.put("email", user.getEmail());
        userProfile.put("mobileNumber", user.getMobilenumber());
        userProfile.put("profilePicture", profilePicture);  // Add profile picture to response
        userProfile.put("lastLogin", user.getLastLogin());  // Add last login time to response
       userProfile.put("Role", user.getRole());
       System.out.println(user.getRole());
        return userProfile;
    }

    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<String> updateMobileNumber(@RequestParam("mobileNumber") Long mobileNumber, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("User not authenticated");
        }

        String email;
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
            email = token.getPrincipal().getAttribute("email");
        } else if (authentication instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
            email = ((MyUser) token.getPrincipal()).getEmail();
        } else {
            return ResponseEntity.status(401).body("Unsupported authentication type");
        }

        Optional<MyUser> userOptional = myUserRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            MyUser user = userOptional.get();
            if (mobileNumber != null) {
                user.setMobilenumber(mobileNumber);
                myUserRepository.save(user);
                return ResponseEntity.ok("Mobile number updated successfully");
            } else {
                return ResponseEntity.badRequest().body("Mobile number cannot be null");
            }
        } else {
            return ResponseEntity.status(404).body("User not found");
        }
    }

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        List<MyUser> users = myUserRepository.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("totalUsers", users.size());
        response.put("users", users);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/admin/assign-role")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<String> assignRole(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }

        Optional<MyUser> userOptional = myUserRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            MyUser user = userOptional.get();
            if ("ROLE_ADMIN".equals(user.getRole())) {
                return ResponseEntity.badRequest().body("User already has ADMIN role");
            }
            user.setRole("ROLE_ADMIN");
            myUserRepository.save(user);
            return ResponseEntity.ok("Admin role assigned successfully");
        } else {
            return ResponseEntity.status(404).body("User not found");
        }
    }

    @DeleteMapping("/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<String> deleteUser(@PathVariable("id") Long id) {
        Optional<MyUser> userOptional = myUserRepository.findById(id);
        if (userOptional.isPresent()) {
            myUserRepository.deleteById(id);
            return ResponseEntity.ok("User deleted successfully");
        } else {
            return ResponseEntity.status(404).body("User not found");
        }
    }

    @PutMapping("/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody Map<String, String> request) {
        Optional<MyUser> userOptional = myUserRepository.findById(id);
        if (userOptional.isPresent()) {
            MyUser user = userOptional.get();
            String username = request.get("username");
            String email = request.get("email");
            String profilePicture = request.get("profilePicture");  // Get profile picture from request

            if (username != null && !username.isEmpty()) {
                user.setUsername(username);
            }
            if (email != null && !email.isEmpty()) {
                user.setEmail(email);
            }
            if (profilePicture != null && !profilePicture.isEmpty()) {
                user.setProfilePicture(profilePicture);  // Update profile picture if provided
            }

            myUserRepository.save(user);
            return ResponseEntity.ok("User updated successfully");
        } else {
            return ResponseEntity.status(404).body("User not found");
        }
    }
    
    
    
    @GetMapping("/current-user")
    public Map<String, Object> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        MyUser user = myUserRepository.findByUsername(username).orElse(null);

        Map<String, Object> response = new HashMap<>();
        if (user != null) {
            response.put("username", user.getUsername());
            response.put("roles", user.getRole());
            response.put("email", user.getEmail());
            response.put("profilePicture", user.getProfilePicture());
        } else {
            response.put("error", "User not found");
        }

        return response;
    }
}
