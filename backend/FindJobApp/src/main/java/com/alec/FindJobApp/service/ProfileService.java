package com.alec.FindJobApp.service;

import com.alec.FindJobApp.dto.ProfileDTO;
import com.alec.FindJobApp.exception.BadRequestException;
import com.alec.FindJobApp.model.Profile;
import com.alec.FindJobApp.model.User;
import com.alec.FindJobApp.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {

  private final ProfileRepository profileRepository;
  private final UserService userService;

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

  @Transactional
  public ProfileDTO getMyProfile() {
    User user = userService.getCurrentUser();
    Profile profile = profileRepository.findByUser(user)
        .orElseGet(() -> createEmptyProfile(user));
    return toDTO(profile);
  }

  public ProfileDTO getProfileByUserId(Long userId) {
    Profile profile = profileRepository.findByUserId(userId)
        .orElseGet(() -> {
          User user = userService.getUserById(userId);
          return createEmptyProfile(user);
        });
    return toDTO(profile);
  }

  private Profile createEmptyProfile(User user) {
    Profile profile = Profile.builder()
        .user(user)
        .build();
    return profileRepository.save(profile);
  }

  @Transactional
  public ProfileDTO updateProfile(ProfileDTO profileDTO) {
    User user = userService.getCurrentUser();
    Profile profile = profileRepository.findByUser(user)
        .orElseGet(() -> createEmptyProfile(user));

    if (!profile.getUser().getId().equals(user.getId())) {
      throw new BadRequestException("You can only update your own profile");
    }

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
