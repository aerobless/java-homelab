package com.sixtymeters.homelab.domain.repository;

import com.sixtymeters.homelab.domain.UserInformation;

import java.util.Optional;
import java.util.UUID;

public interface UserInformationRepository {
    Optional<UserInformation> findById(UUID id);

    void save(UserInformation userInformation);
}
