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

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

  Page<Job> findByRecruiter(User recruiter, Pageable pageable);

  Page<Job> findByStatus(JobStatus status, Pageable pageable);

  Page<Job> findByJobType(JobType jobType, Pageable pageable);

  @Query("SELECT j FROM Job j WHERE " +
      "LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
      "LOWER(j.company) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
      "LOWER(j.location) LIKE LOWER(CONCAT('%', :keyword, '%'))")
  Page<Job> searchJobs(@Param("keyword") String keyword, Pageable pageable);

  Page<Job> findByStatusOrderByCreatedAtDesc(JobStatus status, Pageable pageable);

  Page<Job> findByLocationContainingIgnoreCase(String location, Pageable pageable);

  long countByRecruiter(User recruiter);

  Page<Job> findByStatusAndJobType(JobStatus status, JobType jobType, Pageable pageable);
}
