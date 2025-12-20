package com.alec.FindJobApp.service;

import com.alec.FindJobApp.dto.AuthResponse;
import com.alec.FindJobApp.dto.LoginRequest;
import com.alec.FindJobApp.dto.RegisterRequest;
import com.alec.FindJobApp.dto.ResetPasswordRequest;
import com.alec.FindJobApp.exception.BadRequestException;
import com.alec.FindJobApp.model.PasswordResetToken;
import com.alec.FindJobApp.model.Role;
import com.alec.FindJobApp.model.User;
import com.alec.FindJobApp.repository.PasswordResetTokenRepository;
import com.alec.FindJobApp.repository.UserRepository;
import com.alec.FindJobApp.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordResetTokenRepository passwordResetTokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtils jwtUtils;
  private final AuthenticationManager authenticationManager;
  private final UserDetailsService userDetailsService;
  private final EmailService emailService;
  private final OtpService otpService;

  @Value("${app.frontend.url:http://localhost:5173}")
  private String frontendUrl;

  @Transactional
  public AuthResponse register(RegisterRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new BadRequestException("Email already registered");
    }

    boolean needsApproval = request.getRole() == Role.RECRUITER;

    User user = User.builder()
        .firstName(request.getFirstName())
        .lastName(request.getLastName())
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .role(request.getRole())
        .isActive(true)
        .isApproved(!needsApproval)
        .build();

    user = userRepository.save(user);

    emailService.sendWelcomeEmail(
        user.getEmail(),
        user.getFullName(),
        user.getRole().name());

    if (needsApproval) {
      return AuthResponse.builder()
          .token(null)
          .type("Bearer")
          .id(user.getId())
          .email(user.getEmail())
          .firstName(user.getFirstName())
          .lastName(user.getLastName())
          .role(user.getRole())
          .requiresApproval(true)
          .build();
    }

    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
    String token = jwtUtils.generateToken(userDetails);

    return AuthResponse.builder()
        .token(token)
        .type("Bearer")
        .id(user.getId())
        .email(user.getEmail())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .role(user.getRole())
        .build();
  }

  public AuthResponse login(LoginRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new BadRequestException("User not found"));

    if (!Boolean.TRUE.equals(user.getIsActive())) {
      throw new BadRequestException("Your account has been deactivated. Please contact support.");
    }

    if (user.requiresApproval()) {
      throw new BadRequestException("Your recruiter account is pending approval. Please wait for admin approval.");
    }

    if (Boolean.TRUE.equals(user.getTwoFactorEnabled())) {
      otpService.generateAndSendOtp(user);
      return AuthResponse.builder()
          .token(null)
          .type("Bearer")
          .id(user.getId())
          .email(user.getEmail())
          .firstName(user.getFirstName())
          .lastName(user.getLastName())
          .role(user.getRole())
          .requires2FA(true)
          .build();
    }

    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
    String token = jwtUtils.generateToken(userDetails);

    return AuthResponse.builder()
        .token(token)
        .type("Bearer")
        .id(user.getId())
        .email(user.getEmail())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .role(user.getRole())
        .build();
  }

  public AuthResponse verifyOtpAndLogin(String email, String otpCode) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new BadRequestException("User not found"));

    if (!Boolean.TRUE.equals(user.getIsActive())) {
      throw new BadRequestException("Your account has been deactivated. Please contact support.");
    }

    if (!otpService.verifyOtp(user, otpCode)) {
      throw new BadRequestException("Invalid or expired OTP code");
    }

    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
    String token = jwtUtils.generateToken(userDetails);

    return AuthResponse.builder()
        .token(token)
        .type("Bearer")
        .id(user.getId())
        .email(user.getEmail())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .role(user.getRole())
        .build();
  }

  @Transactional
  public void forgotPassword(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new BadRequestException("No account found with this email address"));

    passwordResetTokenRepository.findByUserAndUsedFalse(user)
        .ifPresent(token -> {
          token.setUsed(true);
          passwordResetTokenRepository.save(token);
        });

    String tokenString = UUID.randomUUID().toString();
    PasswordResetToken resetToken = PasswordResetToken.builder()
        .token(tokenString)
        .user(user)
        .expiryDate(LocalDateTime.now().plusMinutes(30))
        .used(false)
        .build();

    passwordResetTokenRepository.save(resetToken);

    String resetLink = frontendUrl + "/reset-password?token=" + tokenString;
    emailService.sendPasswordResetEmail(user.getEmail(), user.getFullName(), resetLink);
  }

  @Transactional
  public void resetPassword(ResetPasswordRequest request) {
    if (!request.getNewPassword().equals(request.getConfirmPassword())) {
      throw new BadRequestException("Passwords do not match");
    }

    PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
        .orElseThrow(() -> new BadRequestException("Invalid or expired reset token"));

    if (!resetToken.isValid()) {
      throw new BadRequestException("Reset token has expired or already been used");
    }

    User user = resetToken.getUser();
    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    userRepository.save(user);

    resetToken.setUsed(true);
    passwordResetTokenRepository.save(resetToken);
  }
}
