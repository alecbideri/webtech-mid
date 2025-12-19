package com.alec.FindJobApp.service;

import com.alec.FindJobApp.dto.UserDTO;
import com.alec.FindJobApp.exception.ResourceNotFoundException;
import com.alec.FindJobApp.model.Role;
import com.alec.FindJobApp.model.User;
import com.alec.FindJobApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for user operations.
 */
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  /**
   * Gets the currently authenticated user.
   */
  public User getCurrentUser() {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
  }

  /**
   * Gets a user by ID.
   */
  public User getUserById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
  }

  /**
   * Converts User entity to UserDTO.
   */
  public UserDTO toDTO(User user) {
    return UserDTO.builder()
        .id(user.getId())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .email(user.getEmail())
        .role(user.getRole())
        .isActive(user.getIsActive())
        .createdAt(user.getCreatedAt())
        .build();
  }

  /**
   * Gets all users (Admin only).
   */
  public Page<UserDTO> getAllUsers(Pageable pageable) {
    return userRepository.findAll(pageable).map(this::toDTO);
  }

  /**
   * Gets users by role.
   */
  public Page<UserDTO> getUsersByRole(Role role, Pageable pageable) {
    return userRepository.findByRole(role, pageable).map(this::toDTO);
  }

  /**
   * Updates a user.
   */
  @Transactional
  public UserDTO updateUser(Long id, String firstName, String lastName) {
    User user = getUserById(id);

    if (firstName != null && !firstName.isBlank()) {
      user.setFirstName(firstName);
    }
    if (lastName != null && !lastName.isBlank()) {
      user.setLastName(lastName);
    }

    return toDTO(userRepository.save(user));
  }

  /**
   * Deactivates a user (Admin only).
   */
  @Transactional
  public void deactivateUser(Long id) {
    User user = getUserById(id);
    user.setIsActive(false);
    userRepository.save(user);
  }

  /**
   * Activates a user (Admin only).
   */
  @Transactional
  public void activateUser(Long id) {
    User user = getUserById(id);
    user.setIsActive(true);
    userRepository.save(user);
  }

  /**
   * Deletes a user (Admin only).
   */
  @Transactional
  public void deleteUser(Long id) {
    User user = getUserById(id);
    userRepository.delete(user);
  }

  /**
   * Searches users by first name, last name, or email.
   */
  public Page<UserDTO> searchUsers(String query, Pageable pageable) {
    return userRepository.searchUsers(query, pageable).map(this::toDTO);
  }

  /**
   * Searches users by query and filters by role.
   */
  public Page<UserDTO> searchUsersByRole(String query, Role role, Pageable pageable) {
    return userRepository.searchUsersByRole(query, role, pageable).map(this::toDTO);
  }
}
