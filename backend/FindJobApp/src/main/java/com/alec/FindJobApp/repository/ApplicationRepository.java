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

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

  Page<Application> findBySeeker(User seeker, Pageable pageable);

  Page<Application> findByJob(Job job, Pageable pageable);

  Page<Application> findByStatus(ApplicationStatus status, Pageable pageable);

  Optional<Application> findByJobAndSeeker(Job job, User seeker);

  boolean existsByJobAndSeeker(Job job, User seeker);

  Page<Application> findBySeekerAndStatus(User seeker, ApplicationStatus status, Pageable pageable);

  long countByJob(Job job);

  long countBySeeker(User seeker);
}
