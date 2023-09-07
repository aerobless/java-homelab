package com.sixtymeters.homelab.domain.service;

import com.sixtymeters.homelab.domain.UserInformation;
import com.sixtymeters.homelab.domain.repository.UserInformationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DomainUserInformationServiceTest {
    private UserInformationRepository userInformationRepository;
    private DomainUserInformationService tested;
    @BeforeEach
    void setUp() {
        userInformationRepository = mock(UserInformationRepository.class);
        tested = new DomainUserInformationService(userInformationRepository);
    }

    @Test
    void shouldCreateUserInformation_thenSaveIt() {
        final UUID userInformationId = tested.createUserInformation();

        verify(userInformationRepository).save(any(UserInformation.class));
        assertNotNull(userInformationId);
    }

}