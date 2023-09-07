package com.sixtymeters.homelab.application;

import com.sixtymeters.homelab.domain.service.UserInformationService;
import com.sixtymeters.homelab.userinformation.generated.api.UserInformationApi;
import com.sixtymeters.homelab.userinformation.generated.model.GenUserInformationRecordRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserInformationController implements UserInformationApi {

    private final UserInformationService userInformationService;

    @Override
    public ResponseEntity<Void> postUserInformation(GenUserInformationRecordRequest request) {
        //TODO: should do this only once
        UUID userInformationUuid = userInformationService.createUserInformation();
        userInformationService.addRecord(userInformationUuid, request.getKey(), request.getValue());
        return ResponseEntity.ok().build();
    }
}
