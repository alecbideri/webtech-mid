package com.alec.FindJobApp.service;

import com.alec.FindJobApp.dto.ProfileDTO;
import com.alec.FindJobApp.exception.BadRequestException;
import com.alec.FindJobApp.model.Profile;
import com.alec.FindJobApp.model.User;
import com.alec.FindJobApp.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for profile operations.
 */
@Service
@RequiredArgsConstructor
public class ProfileService {

  private final ProfileRepository profileRepository;
  private final UserService userService;

  /**
   * Converts Profile entity to ProfileDTO.
   */
  public ProfileDTO toDTO(Profile profile) {
    return ProfileDTO.builder()
        .id(profile.getId())
        .userId(profile.getUser().getId())
        .bio(profile.getBio())
        .phoneNumber(profile.getPhoneNumber())
        .skills(profile.getSkills())
        .experience(profile.getExperience())
        .education(profile.getEducation())
        .linkedinUrl(profile.getLinkedinUrl())
        .portfolioUrl(profile.getPortfolioUrl())
        .companyName(profile.getCompanyName())
        .companyDescription(profile.getCompanyDescription())
        .build();
  }

  /**
   * Gets the profile for the current user, creating one if it doesn't exist.
   */
  @Transactional
  public ProfileDTO getMyProfile() {
    User user = userService.getCurrentUser();
    Profile profile = profileRepository.findByUser(user)
        .orElseGet(() -> createEmptyProfile(user));
    return toDTO(profile);
  }

  /**
   * Gets a profile by user ID.
   */
  public ProfileDTO getProfileByUserId(Long userId) {
    Profile profile = profileRepository.findByUserId(userId)
        .orElseGet(() -> {
          User user = userService.getUserById(userId);
          return createEmptyProfile(user);
        });
    return toDTO(profile);
  }

  /**
   * Creates an empty profile for a user.
   */
  private Profile createEmptyProfile(User user) {
    Profile profile = Profile.builder()
        .user(user)
        .build();
    return profileRepository.save(profile);
  }

  /**
   * Updates the current user's profile.
   */
  @Transactional
  public ProfileDTO updateProfile(ProfileDTO profileDTO) {
    User user = userService.getCurrentUser();
    Profile profile = profileRepository.findByUser(user)
        .orElseGet(() -> createEmptyProfile(user));

    // Verify ownership
    if (!profile.getUser().getId().equals(user.getId())) {
      throw new BadRequestException("You can only update your own profile");
    }

    // Update fields
    if (profileDTO.getBio() != null) {
      profile.setBio(profileDTO.getBio());
    }
    if (profileDTO.getPhoneNumber() != null) {
      profile.setPhoneNumber(profileDTO.getPhoneNumber());
    }
    if (profileDTO.getSkills() != null) {
      profile.setSkills(profileDTO.getSkills());
    }
    if (profileDTO.getExperience() != null) {
      profile.setExperience(profileDTO.getExperience());
    }
    if (profileDTO.getEducation() != null) {
      profile.setEducation(profileDTO.getEducation());
    }
    if (profileDTO.getLinkedinUrl() != null) {
      profile.setLinkedinUrl(profileDTO.getLinkedinUrl());
    }
    if (profileDTO.getPortfolioUrl() != null) {
      profile.setPortfolioUrl(profileDTO.getPortfolioUrl());
    }
    if (profileDTO.getCompanyName() != null) {
      profile.setCompanyName(profileDTO.getCompanyName());
    }
    if (profileDTO.getCompanyDescription() != null) {
      profile.setCompanyDescription(profileDTO.getCompanyDescription());
    }

    return toDTO(profileRepository.save(profile));
  }
}
