package com.sixtymeters.homelab.domain.service;

import com.sixtymeters.homelab.domain.UserInformation;
import com.sixtymeters.homelab.domain.repository.UserInformationRepository;

import java.util.UUID;

public class DomainUserInformationService implements UserInformationService {

    private final UserInformationRepository userInformationRepository;

    public DomainUserInformationService(UserInformationRepository userInformationRepository) {
        this.userInformationRepository = userInformationRepository;
    }

    @Override
    public UUID createUserInformation() {
        UserInformation userInformation = new UserInformation();
        userInformationRepository.save(userInformation);
        return userInformation.getUuid();
    }

    @Override
    public void addRecord(UUID uuid, String key, String value) {
        userInformationRepository.findById(uuid).orElseThrow().addUserInformation(key, value);
    }

    @Override
    public String printUserInformation(UUID uuid) {
        return userInformationRepository.findById(uuid).orElseThrow().printUserInformation();
    }
}
