package com.genuinecoder.learnspringsecurity.controller;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.genuinecoder.learnspringsecurity.exception.JobPostingNotFoundException;
import com.genuinecoder.learnspringsecurity.model.JobApplication;
import com.genuinecoder.learnspringsecurity.model.JobPosting;
import com.genuinecoder.learnspringsecurity.model.Notification;
import com.genuinecoder.learnspringsecurity.repository.JobApplicationRepository;
import com.genuinecoder.learnspringsecurity.repository.JobPostingRepository;
import com.genuinecoder.learnspringsecurity.repository.NotificationRepository;
import com.genuinecoder.learnspringsecurity.util.ResponseStructure;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/job-postings")
public class JobPostingController {

    @Autowired
    private JobPostingRepository jobPostingRepository;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private NotificationRepository notificationRepository;
    // Existing endpoints...

 // 1. User Endpoint - jobPosting.setLive(false);
    @PostMapping("/user/create")
    public ResponseEntity<ResponseStructure<JobPosting>> createJobPostingAsUser(@RequestBody JobPosting jobPosting) {
        jobPosting.setLive(false); // Always set job as not live for user
        JobPosting savedJobPosting = jobPostingRepository.save(jobPosting);

        ResponseStructure<JobPosting> responseStructure = new ResponseStructure<>();
        responseStructure.setStatus(HttpStatus.CREATED.value());
        responseStructure.setMessage("Job Posting Created Successfully, but it is Not Live");
        responseStructure.setData(savedJobPosting);

        return new ResponseEntity<>(responseStructure, HttpStatus.CREATED);
    }

    @PostMapping("/admin/create")
    @PreAuthorize("hasRole('ADMIN')")  // Ensure only admin can access
    public ResponseEntity<ResponseStructure<JobPosting>> createJobPostingAsAdmin(@RequestBody JobPosting jobPosting) {
        // Admin sets live status directly
    	jobPosting.setLive(true);
        JobPosting savedJobPosting = jobPostingRepository.save(jobPosting);

        // If job posting is live, create a corresponding notification
      
            Notification notification = new Notification();

            notification.setJobId(savedJobPosting.getId());
         
            // Save the notification to the repository
            notificationRepository.save(notification);
        

        ResponseStructure<JobPosting> responseStructure = new ResponseStructure<>();
        responseStructure.setStatus(HttpStatus.CREATED.value());
        responseStructure.setMessage("Job Posting Created Successfully with Admin Defined Live Status");

        // Optionally, you could add a message about notification creation
        if (jobPosting.isLive()) {
            responseStructure.setMessage("Job Posting Created and Notification Sent");
        }

        responseStructure.setData(savedJobPosting);

        return new ResponseEntity<>(responseStructure, HttpStatus.CREATED);
    }

    @PutMapping("/toggle-live/{id}")
    @PreAuthorize("hasRole('ADMIN')")  // Ensure only admin can access
    public ResponseEntity<ResponseStructure<JobPosting>> toggleJobPostingLiveStatus(@PathVariable Long id) {
        Optional<JobPosting> optionalJobPosting = jobPostingRepository.findById(id);

        if (optionalJobPosting.isPresent()) {
            JobPosting jobPosting = optionalJobPosting.get();
            
            // Toggle live status
            jobPosting.setLive(!jobPosting.isLive());
            JobPosting updatedJobPosting = jobPostingRepository.save(jobPosting);

            // Handle notifications: if job is inactive, delete associated notifications
            if (!jobPosting.isLive()) {
                List<Notification> notifications = notificationRepository.findByJobId(jobPosting.getId());
                if (!notifications.isEmpty()) {
                    notificationRepository.deleteAll(notifications);  // Deletes all related notifications
                }
            } else {
                // If job becomes active, create a new notification
                String notificationMessage = "Job posting for job ID " + jobPosting.getId() + " is now live.";
                Notification newNotification = new Notification(jobPosting.getId());
                notificationRepository.save(newNotification);
            }

            String statusMessage = jobPosting.isLive() ? "Job Posting is Now Live" : "Job Posting is Now Not Live";

            ResponseStructure<JobPosting> responseStructure = new ResponseStructure<>();
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage(statusMessage);
            responseStructure.setData(updatedJobPosting);

            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        } else {
            ResponseStructure<JobPosting> responseStructure = new ResponseStructure<>();
            responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
            responseStructure.setMessage("Job Posting Not Found");

            return new ResponseEntity<>(responseStructure, HttpStatus.NOT_FOUND);
        }
    }



    @GetMapping("/{id}")
    public ResponseEntity<ResponseStructure<Object>> getJobPosting(@PathVariable Long id) {
        return jobPostingRepository.findById(id)
                .map(jobPosting -> {
                    ResponseStructure<Object> responseStructure = new ResponseStructure<>();
                    
                    if (jobPosting.isLive()) {
                        // Job is live
                        responseStructure.setStatus(HttpStatus.OK.value());
                        responseStructure.setMessage("Job Posting Found");
                        responseStructure.setData(jobPosting);
                        return ResponseEntity.ok(responseStructure);
                    } else {
                        // Job exists but is not live
                        responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
                        responseStructure.setMessage("Job Posting is not live");
                        responseStructure.setData(null);
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseStructure);
                    }
                })
                .orElseGet(() -> {
                    // Job not found
                    ResponseStructure<Object> responseStructure = new ResponseStructure<>();
                    responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
                    responseStructure.setMessage("Job Posting not found with id " + id);
                    responseStructure.setData(null);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseStructure);
                });
    }


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseStructure<List<JobPosting>>> getAllJobPostings() {
        List<JobPosting> jobPostings = jobPostingRepository.findAll();
        ResponseStructure<List<JobPosting>> responseStructure = new ResponseStructure<>();
        responseStructure.setStatus(HttpStatus.OK.value());
        responseStructure.setMessage("All Job Postings");
        responseStructure.setData(jobPostings);
        return ResponseEntity.ok(responseStructure);
    }

    @GetMapping ("/forusers")
    public ResponseEntity<ResponseStructure<List<JobPosting>>> getAllLiveJobPostings() {
        List<JobPosting> jobPostings = jobPostingRepository.findAll()
            .stream()
            .filter(JobPosting::isLive)  // Filter job postings to only include live ones
            .collect(Collectors.toList());

        ResponseStructure<List<JobPosting>> responseStructure = new ResponseStructure<>();
        responseStructure.setStatus(HttpStatus.OK.value());
        responseStructure.setMessage("Live Job Postings");
        responseStructure.setData(jobPostings);

        return ResponseEntity.ok(responseStructure);
    }

    
    @PutMapping("/{id}")
    public ResponseEntity<ResponseStructure<JobPosting>> updateJobPosting(@PathVariable Long id, @RequestBody JobPosting jobPosting) {
        return jobPostingRepository.findById(id)
                .map(existingJobPosting -> {
                    // Set the ID of the job posting
                    jobPosting.setId(id);
                    
                    // Save the updated job posting
                    JobPosting updatedJobPosting = jobPostingRepository.save(jobPosting);
                    
                    // Find and update related notifications
                    
                    // Build response
                    ResponseStructure<JobPosting> responseStructure = new ResponseStructure<>();
                    responseStructure.setStatus(HttpStatus.OK.value());
                    responseStructure.setMessage("Job Posting and related notifications updated successfully");
                    responseStructure.setData(updatedJobPosting);
                    
                    return ResponseEntity.ok(responseStructure);
                })
                .orElseThrow(() -> new JobPostingNotFoundException("Job Posting not found with id " + id));
    }


    // Delete JobPosting and its notifications

@Transactional  // Ensure that all database operations happen within a transaction
@DeleteMapping("/{id}")
public ResponseEntity<ResponseStructure<String>> deleteJobPosting(@PathVariable Long id) {
    return jobPostingRepository.findById(id)
            .map(existingJobPosting -> {
                try {
                    // Delete related notifications first
                    notificationRepository.deleteByJobId(id);

                    // Delete job posting
                    jobPostingRepository.delete(existingJobPosting);

                    // Build response
                    ResponseStructure<String> responseStructure = new ResponseStructure<>();
                    responseStructure.setStatus(HttpStatus.NO_CONTENT.value());
                    responseStructure.setMessage("Job Posting and related notifications deleted successfully");
                    responseStructure.setData("Deleted Job Posting with id " + id);
                    return ResponseEntity.ok(responseStructure);

                } catch (Exception e) {
                    // Handle any exceptions that occur during the deletion process
                    ResponseStructure<String> errorResponse = new ResponseStructure<>();
                    errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    errorResponse.setMessage("Failed to delete job posting and related notifications");
                    errorResponse.setData(e.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                }
            })
            .orElseThrow(() -> new JobPostingNotFoundException("Job Posting not found with id " + id));
}
    @PostMapping("/{id}/apply")
    public ResponseEntity<ResponseStructure<String>> applyForJob(@PathVariable Long id, @RequestBody String applicantEmail) {
        if (!jobPostingRepository.existsById(id)) {
            throw new JobPostingNotFoundException("Job Posting not found with id " + id);
        }
        JobApplication application = new JobApplication(id, applicantEmail);
        jobApplicationRepository.save(application);
        ResponseStructure<String> responseStructure = new ResponseStructure<>();
        responseStructure.setStatus(HttpStatus.CREATED.value());
        responseStructure.setMessage("Job Application Submitted Successfully");
        responseStructure.setData("Application for job posting id " + id + " by " + applicantEmail);
        return new ResponseEntity<>(responseStructure, HttpStatus.CREATED);
    }

    @GetMapping("/{id}/applications")
    public ResponseEntity<ResponseStructure<List<JobApplication>>> getJobApplications(@PathVariable Long id) {
        List<JobApplication> applications = jobApplicationRepository.findByJobPostingId(id);
        ResponseStructure<List<JobApplication>> responseStructure = new ResponseStructure<>();
        responseStructure.setStatus(HttpStatus.OK.value());
        responseStructure.setMessage("Job Applications Found");
        responseStructure.setData(applications);
        return ResponseEntity.ok(responseStructure);
    }

    // New endpoint to select multiple job postings
    @GetMapping("/select")
    public ResponseEntity<ResponseStructure<List<JobPosting>>> selectJobPostings(@RequestParam List<Long> ids) {
        List<JobPosting> selectedJobPostings = jobPostingRepository.findAllById(ids);
        ResponseStructure<List<JobPosting>> responseStructure = new ResponseStructure<>();
        responseStructure.setStatus(HttpStatus.OK.value());
        responseStructure.setMessage("Selected Job Postings Found");
        responseStructure.setData(selectedJobPostings);
        return ResponseEntity.ok(responseStructure);
    }

    // New endpoint to delete multiple job postings
    @DeleteMapping("/delete-selected")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseStructure<String>> deleteSelectedJobPostings(@RequestBody List<Long> ids) {
        List<JobPosting> jobPostingsToDelete = jobPostingRepository.findAllById(ids);
        if (jobPostingsToDelete.isEmpty()) {
            throw new JobPostingNotFoundException("No Job Postings found with provided IDs");
        }
        jobPostingRepository.deleteAll(jobPostingsToDelete);
        ResponseStructure<String> responseStructure = new ResponseStructure<>();
        responseStructure.setStatus(HttpStatus.NO_CONTENT.value());
        responseStructure.setMessage("Job Postings Deleted Successfully");
        responseStructure.setData("Deleted Job Postings with IDs: " + ids.stream().map(String::valueOf).collect(Collectors.joining(", ")));
        return ResponseEntity.ok(responseStructure);
    }
}