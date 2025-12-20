package com.alec.FindJobApp.controller;

import com.alec.FindJobApp.dto.ApiResponse;
import com.alec.FindJobApp.dto.AuthResponse;
import com.alec.FindJobApp.dto.ForgotPasswordRequest;
import com.alec.FindJobApp.dto.LoginRequest;
import com.alec.FindJobApp.dto.OtpVerifyRequest;
import com.alec.FindJobApp.dto.RegisterRequest;
import com.alec.FindJobApp.dto.ResetPasswordRequest;
import com.alec.FindJobApp.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
    AuthResponse response = authService.register(request);
    String message = Boolean.TRUE.equals(response.getRequiresApproval())
        ? "Registration successful. Your recruiter account is pending admin approval."
        : "Registration successful";
    return ResponseEntity.ok(ApiResponse.success(message, response));
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
    AuthResponse response = authService.login(request);
    String message = Boolean.TRUE.equals(response.getRequires2FA())
        ? "Verification code sent to your email"
        : "Login successful";
    return ResponseEntity.ok(ApiResponse.success(message, response));
  }

  @PostMapping("/verify-otp")
  public ResponseEntity<ApiResponse<AuthResponse>> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
    AuthResponse response = authService.verifyOtpAndLogin(request.getEmail(), request.getOtpCode());
    return ResponseEntity.ok(ApiResponse.success("Login successful", response));
  }

  @PostMapping("/forgot-password")
  public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
    authService.forgotPassword(request.getEmail());
    return ResponseEntity.ok(ApiResponse.success("Password reset email sent", null));
  }

  @PostMapping("/reset-password")
  public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
    authService.resetPassword(request);
    return ResponseEntity.ok(ApiResponse.success("Password reset successful", null));
  }
}
