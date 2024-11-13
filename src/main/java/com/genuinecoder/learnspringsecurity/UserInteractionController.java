package com.genuinecoder.learnspringsecurity;

import com.genuinecoder.learnspringsecurity.model.ContactForm;
import com.genuinecoder.learnspringsecurity.model.ContactFormRepository;
import com.genuinecoder.learnspringsecurity.model.Feedback;
import com.genuinecoder.learnspringsecurity.model.FeedbackRepository;
import com.genuinecoder.learnspringsecurity.model.MyUser;
import com.genuinecoder.learnspringsecurity.model.MyUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/interaction")
public class UserInteractionController {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private MyUserRepository myUserRepository;

    @Autowired
    private ContactFormRepository contactFormRepository;

    // Submit feedback by a user (must have ROLE_USER)
    @PostMapping("/feedback/submit")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> submitFeedback(@RequestParam("message") String message,
                                                 @RequestParam("email") String email) {
        MyUser user = myUserRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        Feedback feedback = new Feedback(user, message);
        feedbackRepository.save(feedback);

        return ResponseEntity.ok("Feedback submitted successfully");
    }

    // Allow users with ROLE_USER to update their feedback
    @PutMapping("/feedback/update/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> updateFeedback(@PathVariable Long id,
                                                 @RequestParam("message") String message,
                                                 @RequestParam("email") String email) {
        MyUser user = myUserRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        Optional<Feedback> feedbackOptional = feedbackRepository.findById(id);
        if (feedbackOptional.isPresent()) {
            Feedback feedback = feedbackOptional.get();
            if (feedback.getUser().getId().equals(user.getId())) {
                feedback.setMessage(message);
                feedbackRepository.save(feedback);
                return ResponseEntity.ok("Feedback updated successfully");
            } else {
                return ResponseEntity.status(403).body("You can only update your own feedback");
            }
        } else {
            return ResponseEntity.status(404).body("Feedback not found");
        }
    }

    // DELETE: Delete individual feedback by ID (admin-only)
    @DeleteMapping("/feedback/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteFeedback(@PathVariable Long id) {
        Optional<Feedback> feedbackOptional = feedbackRepository.findById(id);
        if (feedbackOptional.isPresent()) {
            feedbackRepository.deleteById(id);
            return ResponseEntity.ok("Feedback deleted successfully");
        } else {
            return ResponseEntity.status(404).body("Feedback not found");
        }
    }

    // DELETE: Delete all feedback (admin-only)
    @DeleteMapping("/feedback/delete-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteAllFeedback() {
        feedbackRepository.deleteAll();
        return ResponseEntity.ok("All feedbacks deleted successfully");
    }

    // DELETE: Delete selected feedback by IDs (admin-only)
    @DeleteMapping("/feedback/delete-selected")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteSelectedFeedback(@RequestBody List<Long> ids) {
        feedbackRepository.deleteAllById(ids);
        return ResponseEntity.ok("Selected feedbacks deleted successfully");
    }

    // View feedback by user email (no role required)
    @GetMapping("/feedback/user")
    public List<Feedback> getUserFeedback(@RequestParam("email") String email) {
        MyUser user = myUserRepository.findByEmail(email).orElse(null);
        if (user != null) {
            return feedbackRepository.findByUserId(user.getId());
        }
        return null;
    }

    // Admin view all feedbacks
    @GetMapping("/feedback/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }

    // Submit contact form (open to all)
    @PostMapping("/contact/submit")
    public ResponseEntity<String> submitContactForm(@RequestBody ContactForm contactForm) {
        contactForm.setCreatedAt(LocalDateTime.now());
        contactFormRepository.save(contactForm);
        return ResponseEntity.ok("Contact form submitted successfully");
    }

    // View all contact forms (admin-only)
    @GetMapping("/contact/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ContactForm> getAllContactForms() {
        return contactFormRepository.findAll();
    }

    // DELETE: Delete individual contact form by ID (admin-only)
    @DeleteMapping("/contact/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteContactForm(@PathVariable Long id) {
        Optional<ContactForm> contactFormOptional = contactFormRepository.findById(id);
        if (contactFormOptional.isPresent()) {
            contactFormRepository.deleteById(id);
            return ResponseEntity.ok("Contact form deleted successfully");
        } else {
            return ResponseEntity.status(404).body("Contact form not found");
        }
    }

    // DELETE: Delete all contact forms (admin-only)
    @DeleteMapping("/contact/delete-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteAllContactForms() {
        contactFormRepository.deleteAll();
        return ResponseEntity.ok("All contact forms deleted successfully");
    }

    // DELETE: Delete selected contact forms by IDs (admin-only)
    @DeleteMapping("/contact/delete-selected")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteSelectedContactForms(@RequestBody List<Long> ids) {
        contactFormRepository.deleteAllById(ids);
        return ResponseEntity.ok("Selected contact forms deleted successfully");
    }
}
