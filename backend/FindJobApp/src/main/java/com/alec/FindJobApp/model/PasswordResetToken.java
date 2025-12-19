package com.alec.FindJobApp.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity for storing password reset tokens.
 */
@Entity
@Table(name = "password_reset_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String token;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "expiry_date", nullable = false)
  private LocalDateTime expiryDate;

  @Column(name = "used")
  @Builder.Default
  private Boolean used = false;

  /**
   * Checks if the token has expired.
   */
  public boolean isExpired() {
    return LocalDateTime.now().isAfter(expiryDate);
  }

  /**
   * Checks if the token is valid (not expired and not used).
   */
  public boolean isValid() {
    return !isExpired() && !used;
  }
}
