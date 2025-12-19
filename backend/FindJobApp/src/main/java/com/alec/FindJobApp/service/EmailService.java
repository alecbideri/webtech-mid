package com.alec.FindJobApp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service for sending email notifications.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

  private final JavaMailSender mailSender;

  @Value("${spring.mail.username}")
  private String fromEmail;

  /**
   * Sends a simple email asynchronously.
   */
  @Async
  public void sendEmail(String to, String subject, String body) {
    try {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setFrom(fromEmail);
      message.setTo(to);
      message.setSubject(subject);
      message.setText(body);
      mailSender.send(message);
      log.info("Email sent successfully to: {}", to);
    } catch (Exception e) {
      log.error("Failed to send email to {}: {}", to, e.getMessage());
    }
  }

  /**
   * Sends application status update email.
   */
  public void sendApplicationStatusEmail(String to, String seekerName, String jobTitle,
      String company, String status) {
    String subject = "Application Status Update - " + jobTitle;
    String body = String.format(
        "Dear %s,\n\n" +
            "Your application for the position of %s at %s has been updated.\n\n" +
            "New Status: %s\n\n" +
            "Thank you for using Find Job App.\n\n" +
            "Best regards,\n" +
            "The Find Job App Team",
        seekerName, jobTitle, company, status);
    sendEmail(to, subject, body);
  }

  /**
   * Sends welcome email to new users.
   */
  public void sendWelcomeEmail(String to, String name, String role) {
    String subject = "Welcome to Find Job App!";
    String roleMessage = role.equals("RECRUITER")
        ? "Your account is pending admin approval. You will receive an email once approved."
        : "You can now browse jobs and submit applications.";
    String body = String.format(
        "Dear %s,\n\n" +
            "Welcome to Find Job App! Your account has been successfully created.\n\n" +
            "Account Type: %s\n\n" +
            "%s\n\n" +
            "Best regards,\n" +
            "The Find Job App Team",
        name,
        role,
        roleMessage);
    sendEmail(to, subject, body);
  }

  /**
   * Sends password reset email with reset link.
   */
  public void sendPasswordResetEmail(String to, String name, String resetLink) {
    String subject = "Password Reset Request - Find Job App";
    String body = String.format(
        "Dear %s,\n\n" +
            "We received a request to reset your password for your Find Job App account.\n\n" +
            "Click the link below to reset your password:\n" +
            "%s\n\n" +
            "This link will expire in 30 minutes.\n\n" +
            "If you did not request a password reset, please ignore this email.\n\n" +
            "Best regards,\n" +
            "The Find Job App Team",
        name,
        resetLink);
    sendEmail(to, subject, body);
  }

  /**
   * Sends OTP code email for 2FA.
   */
  public void sendOtpEmail(String to, String name, String otpCode) {
    String subject = "Your Login Verification Code - Find Job App";
    String body = String.format(
        "Dear %s,\n\n" +
            "Your verification code is: %s\n\n" +
            "This code will expire in 5 minutes.\n\n" +
            "If you did not request this code, please secure your account immediately.\n\n" +
            "Best regards,\n" +
            "The Find Job App Team",
        name,
        otpCode);
    sendEmail(to, subject, body);
  }

  /**
   * Sends approval notification to recruiters.
   */
  public void sendRecruiterApprovalEmail(String to, String name) {
    String subject = "Your Recruiter Account Has Been Approved! - Find Job App";
    String body = String.format(
        "Dear %s,\n\n" +
            "Great news! Your recruiter account has been approved by our admin team.\n\n" +
            "You can now log in and start posting jobs on Find Job App.\n\n" +
            "Get started by visiting our website and logging in with your credentials.\n\n" +
            "Best regards,\n" +
            "The Find Job App Team",
        name);
    sendEmail(to, subject, body);
  }

  /**
   * Sends rejection notification to recruiters.
   */
  public void sendRecruiterRejectionEmail(String to, String name) {
    String subject = "Recruiter Account Application Update - Find Job App";
    String body = String.format(
        "Dear %s,\n\n" +
            "We regret to inform you that your recruiter account application has not been approved.\n\n" +
            "If you have any questions, please contact our support team.\n\n" +
            "Best regards,\n" +
            "The Find Job App Team",
        name);
    sendEmail(to, subject, body);
  }
}
