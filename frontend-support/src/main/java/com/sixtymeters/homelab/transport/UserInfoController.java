package com.sixtymeters.homelab.transport;

import com.sixtymeters.homelab.frontend.generated.api.UserinfoApi;
import com.sixtymeters.homelab.frontend.generated.model.GenUserinfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserInfoController implements UserinfoApi {

    @Override
    public ResponseEntity<GenUserinfoResponse> getUserinfo() {
        log.info("Hello World!");
        return null;
    }
}
