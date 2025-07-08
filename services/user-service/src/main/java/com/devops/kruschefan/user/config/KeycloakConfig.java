package com.devops.kruschefan.user.config;

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
                .clientId("user-service")
                .clientSecret("user-service-pw")  
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }
}
