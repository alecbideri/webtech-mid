package com.alec.FindJobApp.service;

import com.alec.FindJobApp.dto.AuthResponse;
import com.alec.FindJobApp.dto.LoginRequest;
import com.alec.FindJobApp.dto.RegisterRequest;
import com.alec.FindJobApp.exception.BadRequestException;
import com.alec.FindJobApp.model.User;
import com.alec.FindJobApp.repository.UserRepository;
import com.alec.FindJobApp.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for authentication operations.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtils jwtUtils;
  private final AuthenticationManager authenticationManager;
  private final UserDetailsService userDetailsService;
  private final EmailService emailService;

  /**
   * Registers a new user.
   */
  @Transactional
  public AuthResponse register(RegisterRequest request) {
    // Check if email already exists
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new BadRequestException("Email already registered");
    }

    // Create new user
    User user = User.builder()
        .firstName(request.getFirstName())
        .lastName(request.getLastName())
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .role(request.getRole())
        .isActive(true)
        .build();

    user = userRepository.save(user);

    // Send welcome email
    emailService.sendWelcomeEmail(
        user.getEmail(),
        user.getFullName(),
        user.getRole().name());

    // Generate JWT token
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

  /**
   * Authenticates a user and returns a JWT token.
   */
  public AuthResponse login(LoginRequest request) {
    // Authenticate
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

    // Get user details
    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new BadRequestException("User not found"));

    // Generate JWT token
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
}
