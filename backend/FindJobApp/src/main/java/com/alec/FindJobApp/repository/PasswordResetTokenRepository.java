package com.alec.FindJobApp.repository;

import com.alec.FindJobApp.model.PasswordResetToken;
import com.alec.FindJobApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for PasswordResetToken entity database operations.
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

  /**
   * Finds a password reset token by its token string.
   */
  Optional<PasswordResetToken> findByToken(String token);

  /**
   * Finds all tokens for a specific user.
   */
  Optional<PasswordResetToken> findByUserAndUsedFalse(User user);

  /**
   * Deletes all tokens for a specific user.
   */
  void deleteByUser(User user);
}
