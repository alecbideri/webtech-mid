package com.alec.FindJobApp.model;

/**
 * Enum representing the different user roles in the application.
 * Used for role-based access control.
 */
public enum Role {
  ADMIN, // System administrator with full access
  RECRUITER, // Can post jobs and manage applications
  SEEKER // Can browse and apply for jobs
}
