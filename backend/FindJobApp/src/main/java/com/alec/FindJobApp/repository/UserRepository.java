package com.alec.FindJobApp.repository;

import com.alec.FindJobApp.model.Role;
import com.alec.FindJobApp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for User entity database operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  /**
   * Finds a user by their email address.
   */
  Optional<User> findByEmail(String email);

  /**
   * Checks if a user with the given email exists.
   */
  boolean existsByEmail(String email);

  /**
   * Finds all users with a specific role.
   */
  Page<User> findByRole(Role role, Pageable pageable);

  /**
   * Finds all active users.
   */
  Page<User> findByIsActiveTrue(Pageable pageable);

  /**
   * Finds users by role and active status.
   */
  Page<User> findByRoleAndIsActiveTrue(Role role, Pageable pageable);
}
