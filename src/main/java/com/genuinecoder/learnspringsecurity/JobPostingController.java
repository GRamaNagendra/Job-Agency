package com.genuinecoder.learnspringsecurity;

import com.genuinecoder.learnspringsecurity.model.JobPosting;
import com.genuinecoder.learnspringsecurity.model.JobPostingRepository;
import com.genuinecoder.learnspringsecurity.model.JobApplication;
import com.genuinecoder.learnspringsecurity.model.JobApplicationRepository;
import com.genuinecoder.learnspringsecurity.util.ResponseStructure;
import com.genuinecoder.learnspringsecurity.exception.JobPostingNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/job-postings")
public class JobPostingController {

    @Autowired
    private JobPostingRepository jobPostingRepository;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseStructure<JobPosting>> createJobPosting(@RequestBody JobPosting jobPosting) {
        JobPosting savedJobPosting = jobPostingRepository.save(jobPosting);
        ResponseStructure<JobPosting> responseStructure = new ResponseStructure<>();
        responseStructure.setStatus(HttpStatus.CREATED.value());
        responseStructure.setMessage("Job Posting Created Successfully");
        responseStructure.setData(savedJobPosting);
        return new ResponseEntity<>(responseStructure, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseStructure<JobPosting>> getJobPosting(@PathVariable Long id) {
        return jobPostingRepository.findById(id)
                .map(jobPosting -> {
                    ResponseStructure<JobPosting> responseStructure = new ResponseStructure<>();
                    responseStructure.setStatus(HttpStatus.OK.value());
                    responseStructure.setMessage("Job Posting Found");
                    responseStructure.setData(jobPosting);
                    return ResponseEntity.ok(responseStructure);
                })
                .orElseThrow(() -> new JobPostingNotFoundException("Job Posting not found with id " + id));
    }

    @GetMapping
    public ResponseEntity<ResponseStructure<List<JobPosting>>> getAllJobPostings() {
        List<JobPosting> jobPostings = jobPostingRepository.findAll();
        ResponseStructure<List<JobPosting>> responseStructure = new ResponseStructure<>();
        responseStructure.setStatus(HttpStatus.OK.value());
        responseStructure.setMessage("All Job Postings");
        responseStructure.setData(jobPostings);
        return ResponseEntity.ok(responseStructure);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseStructure<JobPosting>> updateJobPosting(@PathVariable Long id, @RequestBody JobPosting jobPosting) {
        return jobPostingRepository.findById(id)
                .map(existingJobPosting -> {
                    jobPosting.setId(id);
                    JobPosting updatedJobPosting = jobPostingRepository.save(jobPosting);
                    ResponseStructure<JobPosting> responseStructure = new ResponseStructure<>();
                    responseStructure.setStatus(HttpStatus.OK.value());
                    responseStructure.setMessage("Job Posting Updated Successfully");
                    responseStructure.setData(updatedJobPosting);
                    return ResponseEntity.ok(responseStructure);
                })
                .orElseThrow(() -> new JobPostingNotFoundException("Job Posting not found with id " + id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseStructure<String>> deleteJobPosting(@PathVariable Long id) {
        return jobPostingRepository.findById(id)
                .map(existingJobPosting -> {
                    jobPostingRepository.delete(existingJobPosting);
                    ResponseStructure<String> responseStructure = new ResponseStructure<>();
                    responseStructure.setStatus(HttpStatus.NO_CONTENT.value());
                    responseStructure.setMessage("Job Posting Deleted Successfully");
                    responseStructure.setData("Job Posting with id " + id + " deleted");
                    return ResponseEntity.ok(responseStructure);
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
}
