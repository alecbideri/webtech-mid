package com.alec.FindJobApp.repository;

import com.alec.FindJobApp.model.Job;
import com.alec.FindJobApp.model.JobStatus;
import com.alec.FindJobApp.model.JobType;
import com.alec.FindJobApp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository for Job entity database operations.
 */
@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

  /**
   * Finds all jobs posted by a specific recruiter.
   */
  Page<Job> findByRecruiter(User recruiter, Pageable pageable);

  /**
   * Finds all jobs with a specific status.
   */
  Page<Job> findByStatus(JobStatus status, Pageable pageable);

  /**
   * Finds all jobs of a specific type.
   */
  Page<Job> findByJobType(JobType jobType, Pageable pageable);

  /**
   * Searches jobs by title, company, or location (case-insensitive).
   */
  @Query("SELECT j FROM Job j WHERE " +
      "LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
      "LOWER(j.company) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
      "LOWER(j.location) LIKE LOWER(CONCAT('%', :keyword, '%'))")
  Page<Job> searchJobs(@Param("keyword") String keyword, Pageable pageable);

  /**
   * Finds all open jobs.
   */
  Page<Job> findByStatusOrderByCreatedAtDesc(JobStatus status, Pageable pageable);

  /**
   * Finds jobs by location.
   */
  Page<Job> findByLocationContainingIgnoreCase(String location, Pageable pageable);

  /**
   * Counts jobs by recruiter.
   */
  long countByRecruiter(User recruiter);
}
