package com.sixtymeters.homelab.infrastructure.configuration;

import com.sixtymeters.homelab.domain.repository.UserInformationRepository;
import com.sixtymeters.homelab.domain.service.DomainUserInformationService;
import com.sixtymeters.homelab.domain.service.UserInformationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan()
public class BeanConfiguration {

    @Bean
    UserInformationService orderService(final UserInformationRepository userInformationRepository) {
        return new DomainUserInformationService(userInformationRepository);
    }
}
