package com.alec.FindJobApp.repository;

import com.alec.FindJobApp.model.Profile;
import com.alec.FindJobApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

  Optional<Profile> findByUser(User user);

  Optional<Profile> findByUserId(Long userId);

  boolean existsByUser(User user);
}
