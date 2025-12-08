package com.alec.FindJobApp.controller;

import com.alec.FindJobApp.model.Role;
import com.alec.FindJobApp.model.User;
import com.alec.FindJobApp.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('SYSADMIN')") // Enforce Sysadmin for all endpoints here
@RequiredArgsConstructor
public class AdminController {

  private final AdminService adminService;

  @GetMapping("/users")
  public ResponseEntity<List<User>> getAllUsers(@RequestParam(required = false) Role role) {
    if (role != null) {
      return ResponseEntity.ok(adminService.getProfilesByRole(role));
    }
    return ResponseEntity.ok(adminService.getAllProfiles());
  }

  @PutMapping("/users/{id}/verify")
  public ResponseEntity<String> verifyCreator(@PathVariable Long id) {
    adminService.verifyCreator(id);
    return ResponseEntity.ok("User verified successfully");
  }

  @DeleteMapping("/users/{id}")
  public ResponseEntity<String> deleteUser(@PathVariable Long id) {
    adminService.deleteUser(id);
    return ResponseEntity.ok("User deleted successfully");
  }

  @DeleteMapping("/jobs/{id}")
  public ResponseEntity<String> deleteJob(@PathVariable Long id) {
    adminService.deleteJob(id);
    return ResponseEntity.ok("Job deleted successfully");
  }

  @DeleteMapping("/all")
  public ResponseEntity<String> deleteAll() {
    adminService.deleteAllJobsAndAccounts();
    return ResponseEntity.ok("All jobs and non-admin accounts deleted.");
  }

  @GetMapping("/dashboard")
  public ResponseEntity<Map<String, Object>> getDashboard() {
    return ResponseEntity.ok(adminService.getDashboardAnalytics());
  }
}
