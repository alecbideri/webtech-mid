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

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);

  Page<User> findByRole(Role role, Pageable pageable);

  Page<User> findByIsActiveTrue(Pageable pageable);

  Page<User> findByRoleAndIsActiveTrue(Role role, Pageable pageable);

  List<User> findByRoleAndIsApprovedFalse(Role role);

  @Query("SELECT u FROM User u WHERE " +
      "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
      "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
      "LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))")
  Page<User> searchUsers(@Param("query") String query, Pageable pageable);

  @Query("SELECT u FROM User u WHERE " +
      "(LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
      "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
      "LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))) " +
      "AND u.role = :role")
  Page<User> searchUsersByRole(@Param("query") String query, @Param("role") Role role, Pageable pageable);

  Optional<User> findByProviderAndProviderId(String provider, String providerId);
}
