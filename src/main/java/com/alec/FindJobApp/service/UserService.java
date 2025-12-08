package com.alec.FindJobApp.service;

import com.alec.FindJobApp.model.Role;
import com.alec.FindJobApp.model.User;
import com.alec.FindJobApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public User registerUser(User user) {
    // Check if user exists
    if (userRepository.findByUsername(user.getUsername()).isPresent() ||
        userRepository.findByEmail(user.getEmail()).isPresent()) {
      throw new RuntimeException("Username or Email already exists");
    }

    // Encode password
    user.setPassword(passwordEncoder.encode(user.getPassword()));

    // If Job Creator, set verified to false, else true
    if (user.getRole() == Role.JOB_CREATOR) {
      user.setVerified(false);
    } else {
      user.setVerified(true);
    }

    return userRepository.save(user);
  }

  public List<User> findAllUsers() {
    return userRepository.findAll();
  }

  public List<User> findByRole(Role role) {
    return userRepository.findByRole(role);
  }

  public void deleteUser(Long id) {
    userRepository.deleteById(id);
  }

  public User updateUser(Long id, User updatedUser) {
    User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

    if (updatedUser.getName() != null)
      user.setName(updatedUser.getName());
    if (updatedUser.getEmail() != null)
      user.setEmail(updatedUser.getEmail());
    // For password change, usually a separate method with old password check is
    // better, but simple here:
    if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
      user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
    }

    return userRepository.save(user);
  }

  // Admin specific
  public void verifyCreator(Long id) {
    User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    if (user.getRole() == Role.JOB_CREATOR) {
      user.setVerified(true);
      userRepository.save(user);
    }
  }
}
