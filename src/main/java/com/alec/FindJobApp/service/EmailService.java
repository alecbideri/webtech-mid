package com.alec.FindJobApp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

  private final JavaMailSender mailSender;

  @Value("${spring.mail.username:noreply@findjobapp.com}")
  private String fromEmail;

  @Async
  public void sendEmail(String to, String subject, String body) {
    try {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setFrom(fromEmail);
      message.setTo(to);
      message.setSubject(subject);
      message.setText(body);
      mailSender.send(message);
      System.out.println("Email sent successfully to " + to);
    } catch (Exception e) {
      System.err.println("Error sending email: " + e.getMessage());
      // Don't rethrow to avoid rolling back transaction just for email failure in
      // this context,
      // but ideally we should handle it better.
    }
  }

  public void sendApplicationStatusEmail(String to, String status, String jobTitle) {
    String subject = "Update on your application for " + jobTitle;
    String body = "Your application status has been updated to: " + status;

    if ("CONFIRMED".equalsIgnoreCase(status)) {
      body += "\n\nCongratulations! You have been accepted.";
    } else if ("SCHEDULE_INTERVIEW".equalsIgnoreCase(status)) {
      body += "\n\nWe would like to schedule an interview.";
    }

    sendEmail(to, subject, body);
  }
}
