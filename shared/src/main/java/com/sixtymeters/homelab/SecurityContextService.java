package com.sixtymeters.homelab;

public interface SecurityContextService {
    boolean hasOAuthSecurityToken();

    String getUsername();

    String getEmail();

    String getFirstName();

    String getLastName();
}
