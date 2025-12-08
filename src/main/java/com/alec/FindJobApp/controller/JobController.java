package com.alec.FindJobApp.controller;

import com.alec.FindJobApp.model.Job;
import com.alec.FindJobApp.model.User;
import com.alec.FindJobApp.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @GetMapping
    public ResponseEntity<List<Job>> getAllJobs() {
        return ResponseEntity.ok(jobService.findAllJobs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.findJobById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('JOB_CREATOR')")
    public ResponseEntity<Job> createJob(@RequestBody Job job, @AuthenticationPrincipal UserDetails userDetails) {
        User creator = (User) userDetails;
        return ResponseEntity.ok(jobService.createJob(job, creator));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('JOB_CREATOR')")
    public ResponseEntity<Job> updateJob(@PathVariable Long id, @RequestBody Job job,
            @AuthenticationPrincipal UserDetails userDetails) {
        User creator = (User) userDetails;
        return ResponseEntity.ok(jobService.updateJob(id, job, creator));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User requestor = (User) userDetails;
        jobService.deleteJob(id, requestor);
        return ResponseEntity.ok("Job deleted successfully");
    }

    @GetMapping("/my-jobs")
    @PreAuthorize("hasRole('JOB_CREATOR')")
    public ResponseEntity<List<Job>> getMyJobs(@AuthenticationPrincipal UserDetails userDetails) {
        User creator = (User) userDetails;
        return ResponseEntity.ok(jobService.findJobsByCreator(creator));
    }
}