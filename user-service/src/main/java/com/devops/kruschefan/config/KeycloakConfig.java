package com.devops.kruschefan.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class KeycloakConfig {

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl("http://keycloak:8080")
                .realm("forms-ai")
                .clientId("admin-cli")
                .username("keycloak")
                .password("keycloak-pw")
                .grantType(OAuth2Constants.PASSWORD)
                .build();
    }
}
