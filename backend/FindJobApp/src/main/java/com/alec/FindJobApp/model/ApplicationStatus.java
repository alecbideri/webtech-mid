package com.alec.FindJobApp.model;

/**
 * Enum representing application status.
 */
public enum ApplicationStatus {
  PENDING, // Application submitted, waiting for review
  REVIEWED, // Application has been reviewed
  ACCEPTED, // Application accepted
  REJECTED // Application rejected
}
