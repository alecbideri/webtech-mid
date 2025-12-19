package com.alec.FindJobApp.repository;

import com.alec.FindJobApp.model.Role;
import com.alec.FindJobApp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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

  /**
   * Finds users by role that are not approved (pending approval).
   */
  List<User> findByRoleAndIsApprovedFalse(Role role);

  /**
   * Searches users by first name, last name, or email (case-insensitive).
   */
  @Query("SELECT u FROM User u WHERE " +
      "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
      "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
      "LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))")
  Page<User> searchUsers(@Param("query") String query, Pageable pageable);

  /**
   * Searches users by query and filters by role.
   */
  @Query("SELECT u FROM User u WHERE " +
      "(LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
      "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
      "LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))) " +
      "AND u.role = :role")
  Page<User> searchUsersByRole(@Param("query") String query, @Param("role") Role role, Pageable pageable);

  /**
   * Finds a user by OAuth provider and provider ID.
   */
  Optional<User> findByProviderAndProviderId(String provider, String providerId);
}
