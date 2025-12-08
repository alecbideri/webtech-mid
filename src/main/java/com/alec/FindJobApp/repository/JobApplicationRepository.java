package com.alec.FindJobApp.repository;

import com.alec.FindJobApp.model.Job;
import com.alec.FindJobApp.model.JobApplication;
import com.alec.FindJobApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
  List<JobApplication> findBySeeker(User seeker);

  List<JobApplication> findByJob(Job job);

  Optional<JobApplication> findByJobAndSeeker(Job job, User seeker);
}
