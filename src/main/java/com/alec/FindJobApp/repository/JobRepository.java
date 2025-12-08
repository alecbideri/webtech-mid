package com.alec.FindJobApp.repository;

import com.alec.FindJobApp.model.Job;
import com.alec.FindJobApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
  List<Job> findByCreator(User creator);
  // Sysadmin and Seekers see all, but maybe filtered? standard findAll is enough
  // for now.
}
