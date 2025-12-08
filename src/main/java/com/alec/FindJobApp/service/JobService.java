package com.alec.FindJobApp.service;

import com.alec.FindJobApp.model.Job;
import com.alec.FindJobApp.model.User;
import com.alec.FindJobApp.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;

    public Job createJob(Job job, User creator) {
        if (!creator.isVerified()) {
            throw new RuntimeException("Creator not verified by Admin yet.");
        }
        job.setCreator(creator);
        return jobRepository.save(job);
    }

    public List<Job> findAllJobs() {
        return jobRepository.findAll();
    }

    public List<Job> findJobsByCreator(User creator) {
        return jobRepository.findByCreator(creator);
    }

    public Job findJobById(Long id) {
        return jobRepository.findById(id).orElseThrow(() -> new RuntimeException("Job not found"));
    }

    public Job updateJob(Long id, Job updatedJob, User creator) {
        Job job = findJobById(id);
        if (!job.getCreator().getId().equals(creator.getId())) {
            throw new RuntimeException("Unauthorized to update this job");
        }
        job.setTitle(updatedJob.getTitle());
        job.setDescription(updatedJob.getDescription());
        job.setLocation(updatedJob.getLocation());
        job.setType(updatedJob.getType());
        return jobRepository.save(job);
    }

    public void deleteJob(Long id, User requestor) {
        Job job = findJobById(id);
        // Allow Creator who owns it OR Sysadmin to delete
        boolean isOwner = job.getCreator().getId().equals(requestor.getId());
        boolean isAdmin = requestor.getRole().name().equals("SYSADMIN");

        if (!isOwner && !isAdmin) {
            throw new RuntimeException("Unauthorized to delete this job");
        }

        jobRepository.delete(job);
    }
}