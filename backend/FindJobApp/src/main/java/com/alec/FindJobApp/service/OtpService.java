package com.alec.FindJobApp.service;

import com.alec.FindJobApp.model.User;
import com.alec.FindJobApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

/**
 * Service for handling OTP (One-Time Password) generation and verification for
 * 2FA.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

  private final UserRepository userRepository;
  private final EmailService emailService;
  private static final int OTP_LENGTH = 6;
  private static final int OTP_EXPIRY_MINUTES = 5;
  private final SecureRandom secureRandom = new SecureRandom();

  /**
   * Generates and sends an OTP to the user's email.
   */
  @Transactional
  public void generateAndSendOtp(User user) {
    String otp = generateOtp();
    user.setOtpCode(otp);
    user.setOtpExpiry(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
    userRepository.save(user);

    // Send OTP via email
    emailService.sendOtpEmail(user.getEmail(), user.getFullName(), otp);
    log.info("OTP sent to user: {}", user.getEmail());
  }

  /**
   * Verifies the OTP code for a user.
   */
  @Transactional
  public boolean verifyOtp(User user, String otpCode) {
    if (user.getOtpCode() == null || user.getOtpExpiry() == null) {
      return false;
    }

    // Check if OTP is expired
    if (LocalDateTime.now().isAfter(user.getOtpExpiry())) {
      clearOtp(user);
      return false;
    }

    // Check if OTP matches
    if (user.getOtpCode().equals(otpCode)) {
      clearOtp(user);
      return true;
    }

    return false;
  }

  /**
   * Clears the OTP from the user.
   */
  private void clearOtp(User user) {
    user.setOtpCode(null);
    user.setOtpExpiry(null);
    userRepository.save(user);
  }

  /**
   * Generates a random 6-digit OTP.
   */
  private String generateOtp() {
    int otp = secureRandom.nextInt(900000) + 100000; // 100000 to 999999
    return String.valueOf(otp);
  }
}
