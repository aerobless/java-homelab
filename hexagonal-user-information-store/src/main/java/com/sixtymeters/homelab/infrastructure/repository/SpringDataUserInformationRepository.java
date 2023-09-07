package com.sixtymeters.homelab.infrastructure.repository;

import com.sixtymeters.homelab.domain.UserInformation;
import com.sixtymeters.homelab.domain.repository.UserInformationRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.*;

@Component
@Primary
public class SpringDataUserInformationRepository implements UserInformationRepository {

    //TODO: we would normally access a real database here, for now it's just simulated
    Map<UUID, UserInformation> userInformations = new HashMap<>();

    @Override
    public Optional<UserInformation> findById(UUID id) {
        return userInformations.get(id) == null ? Optional.empty() : Optional.of(userInformations.get(id));
    }

    @Override
    public void save(UserInformation userInformation) {
        userInformations.put(userInformation.getUuid(), userInformation);
    }
}
