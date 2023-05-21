package com.sixtymeters.homelab.security;

import com.sixtymeters.homelab.SecurityContextService;
import com.sixtymeters.homelab.exception.HomelabBaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

@Service("securityContextService")
@RequiredArgsConstructor
public class SecurityContextServiceImpl implements SecurityContextService {
    public static final String USERNAME_ATTRIBUTE = "preferred_username";
    public static final String EMAIL_ATTRIBUTE = "email";
    public static final String FIRST_NAME_ATTRIBUTE = "given_name";
    public static final String LAST_NAME_ATTRIBUTE = "family_name";


    private String getAttribute(String attribute) {
        if (hasOAuthSecurityToken()) {
            var authenticationToken = (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

            return authenticationToken.getPrincipal().getAttribute(attribute);
        }
        throw new HomelabBaseException("Accessing attributes is only possible on a OAuth2AuthenticationToken");
    }

    public boolean hasOAuthSecurityToken() {
        return SecurityContextHolder.getContext().getAuthentication() instanceof OAuth2AuthenticationToken;
    }

    @Override
    public String getUsername() {
        return getAttribute(USERNAME_ATTRIBUTE);
    }

    @Override
    public String getEmail() {
        return getAttribute(EMAIL_ATTRIBUTE);
    }

    @Override
    public String getFirstName() {
        return getAttribute(FIRST_NAME_ATTRIBUTE);
    }

    @Override
    public String getLastName(){
        return getAttribute(LAST_NAME_ATTRIBUTE);
    }
}
