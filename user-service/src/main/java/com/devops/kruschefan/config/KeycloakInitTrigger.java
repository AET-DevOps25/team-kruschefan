package com.devops.kruschefan.config;

import jakarta.annotation.PostConstruct;
import org.keycloak.admin.client.Keycloak;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@DependsOn("keycloak")
public class KeycloakInitTrigger {

    private final Keycloak keycloak;

    public KeycloakInitTrigger(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    @PostConstruct
    public void init() {
        System.out.println("✅ Keycloak bean initialized.");
        try {
            var token = keycloak.tokenManager().getAccessToken().getToken();
            System.out.println("Successfully retrieved Keycloak token: " + token);
        } catch (Exception e) {
            System.err.println("Keycloak connection failed: " + e.getMessage());
            throw e;
        }
    }
}
