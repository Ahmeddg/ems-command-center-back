package com.example.ems_command_center.service;

import com.example.ems_command_center.model.UserProfile;
import com.example.ems_command_center.repository.UserProfileRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public UserProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    public Optional<UserProfile> getProfile() {
        return userProfileRepository.findAll().stream().findFirst();
    }

    public UserProfile save(UserProfile profile) {
        return userProfileRepository.save(profile);
    }
}
