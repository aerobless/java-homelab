package com.sixtymeters.homelab.domain;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class UserInformation {
    private final UUID uuid;
    private final List<UserInformationRecord> userInformationRecords = new ArrayList<>();

    public UserInformation() {
        this.uuid = UUID.randomUUID();
    }

    public void addUserInformation(String key, String value) {
        //validateState();
        userInformationRecords.add(new UserInformationRecord(UUID.randomUUID(), key, value));
    }

    public String printUserInformation() {
        StringBuilder sb = new StringBuilder();
        for (UserInformationRecord userInformationRecord : userInformationRecords) {
            sb.append(userInformationRecord.printRecord());
            sb.append("\n");
        }
        return sb.toString();
    }
}
