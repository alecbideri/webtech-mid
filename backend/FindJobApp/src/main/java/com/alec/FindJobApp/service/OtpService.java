package com.alec.FindJobApp.service;

import com.alec.FindJobApp.model.User;
import com.alec.FindJobApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

  private final UserRepository userRepository;
  private final EmailService emailService;
  private static final int OTP_LENGTH = 6;
  private static final int OTP_EXPIRY_MINUTES = 5;
  private final SecureRandom secureRandom = new SecureRandom();

  @Transactional
  public void generateAndSendOtp(User user) {
    String otp = generateOtp();
    user.setOtpCode(otp);
    user.setOtpExpiry(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
    userRepository.save(user);

    emailService.sendOtpEmail(user.getEmail(), user.getFullName(), otp);
    log.info("OTP sent to user: {}", user.getEmail());
  }

  @Transactional
  public boolean verifyOtp(User user, String otpCode) {
    if (user.getOtpCode() == null || user.getOtpExpiry() == null) {
      return false;
    }

    if (LocalDateTime.now().isAfter(user.getOtpExpiry())) {
      clearOtp(user);
      return false;
    }

    if (user.getOtpCode().equals(otpCode)) {
      clearOtp(user);
      return true;
    }

    return false;
  }

  private void clearOtp(User user) {
    user.setOtpCode(null);
    user.setOtpExpiry(null);
    userRepository.save(user);
  }

  private String generateOtp() {
    int otp = secureRandom.nextInt(900000) + 100000;
    return String.valueOf(otp);
  }
}
