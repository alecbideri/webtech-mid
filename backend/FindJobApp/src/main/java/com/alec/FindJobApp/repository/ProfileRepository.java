package com.alec.FindJobApp.repository;

import com.alec.FindJobApp.model.Profile;
import com.alec.FindJobApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Profile entity database operations.
 */
@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

  /**
   * Finds a profile by user.
   */
  Optional<Profile> findByUser(User user);

  /**
   * Finds a profile by user ID.
   */
  Optional<Profile> findByUserId(Long userId);

  /**
   * Checks if a profile exists for a user.
   */
  boolean existsByUser(User user);
}
