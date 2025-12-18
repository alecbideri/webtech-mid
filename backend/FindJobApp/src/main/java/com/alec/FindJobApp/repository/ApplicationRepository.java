package com.alec.FindJobApp.repository;

import com.alec.FindJobApp.model.Application;
import com.alec.FindJobApp.model.ApplicationStatus;
import com.alec.FindJobApp.model.Job;
import com.alec.FindJobApp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Application entity database operations.
 */
@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

  /**
   * Finds all applications submitted by a specific seeker.
   */
  Page<Application> findBySeeker(User seeker, Pageable pageable);

  /**
   * Finds all applications for a specific job.
   */
  Page<Application> findByJob(Job job, Pageable pageable);

  /**
   * Finds all applications with a specific status.
   */
  Page<Application> findByStatus(ApplicationStatus status, Pageable pageable);

  /**
   * Finds application by job and seeker.
   */
  Optional<Application> findByJobAndSeeker(Job job, User seeker);

  /**
   * Checks if user has already applied for a job.
   */
  boolean existsByJobAndSeeker(Job job, User seeker);

  /**
   * Finds applications by seeker and status.
   */
  Page<Application> findBySeekerAndStatus(User seeker, ApplicationStatus status, Pageable pageable);

  /**
   * Counts applications for a job.
   */
  long countByJob(Job job);

  /**
   * Counts applications by seeker.
   */
  long countBySeeker(User seeker);
}
