package com.sixtymeters.homelab.domain.service;

import java.util.UUID;

public interface UserInformationService {

    UUID createUserInformation();

    void addRecord(UUID uuid, String key, String value);

    String printUserInformation(UUID uuid);

}
