package com.sixtymeters.homelab.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserInformationRecord {

    private final UUID uuid;
    private final String key;
    private final String value;

    public String printRecord() {
        return "Key: %s Value: %s".formatted(key, value);
    }
}
