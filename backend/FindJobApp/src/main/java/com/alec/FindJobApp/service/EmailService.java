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
    String body = String.format(
        "Dear %s,\n\n" +
            "Welcome to Find Job App! Your account has been successfully created.\n\n" +
            "Account Type: %s\n\n" +
            "You can now %s\n\n" +
            "Best regards,\n" +
            "The Find Job App Team",
        name,
        role,
        role.equals("RECRUITER")
            ? "post jobs and manage applications."
            : "browse jobs and submit applications.");
    sendEmail(to, subject, body);
  }
}
