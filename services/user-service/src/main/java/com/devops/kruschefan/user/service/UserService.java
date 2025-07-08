package com.devops.kruschefan.user.service;

import com.devops.kruschefan.user.config.KeycloakConfig;
import com.devops.kruschefan.user.metrics.LoginMetrics;
import com.devops.kruschefan.user.metrics.ProcessingMetrics;
import com.devops.kruschefan.user.metrics.ActiveUserGauge;
import com.devops.kruschefan.user.metrics.PayloadMetrics;
import com.devops.kruschefan.openapi.api.UserApiDelegate;
import com.devops.kruschefan.openapi.model.UserCreateRequest;
import com.devops.kruschefan.openapi.model.UserResponse;
import com.devops.kruschefan.openapi.model.UserUpdateRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.server.ResponseStatusException;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import jakarta.ws.rs.core.Response;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Import(KeycloakConfig.class)
@Slf4j
public class UserService implements UserApiDelegate {

    private final Keycloak keycloak;
    private final LoginMetrics loginMetrics;
    private final ProcessingMetrics processingMetrics;
    private final ActiveUserGauge activeUserGauge;
    private final PayloadMetrics payloadMetrics;
    private final ModelMapper modelMapper;

    @Value("${keycloak.realm:${KEYCLOAK_REALM}}")
    private String realm;

    @Override
    public ResponseEntity<UserResponse> createUser(UserCreateRequest userCreateRequest) {
        log.info("Creating user in Keycloak: {}", userCreateRequest.getUsername());

        payloadMetrics.recordPayloadSize(userCreateRequest.getPassword().length()); // track password size
        processingMetrics.processUserRequest(); // record timing

        UserRepresentation kcUser = getKcUser(userCreateRequest);

        Response response = keycloak.realm(realm).users().create(kcUser);
        if (response.getStatus() != HttpStatus.CREATED.value()) {
            log.error("Failed to create user: {}", userCreateRequest.getUsername());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Failed to create user in Keycloak. Status: " + response.getStatus()
            );
        }

        loginMetrics.recordLogin();
        activeUserGauge.increment();

        String keycloakId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
        kcUser.setId(keycloakId);
        return new ResponseEntity<>(modelMapper.map(kcUser, UserResponse.class), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteUser(String username) {
        UserRepresentation user = getKcUserByUsername(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        keycloak.realm(realm).users().get(user.getId()).remove();
        log.info("Deleted Keycloak user: {}", username);
        activeUserGauge.decrement();

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<UserResponse> getUser(String username) {
        UserRepresentation user = getKcUserByUsername(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        log.info("Found Keycloak user: {}", username);
        return new ResponseEntity<>(modelMapper.map(user, UserResponse.class), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return new ResponseEntity<>(keycloak.realm(realm).users().list().stream()
                .map(user -> modelMapper.map(user, UserResponse.class))
                .collect(Collectors.toList()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserResponse> updateUser(String username, UserUpdateRequest userUpdateRequest) {
        UserRepresentation user = getKcUserByUsername(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        user.setEmail(userUpdateRequest.getEmail());
        user.setFirstName(userUpdateRequest.getFirstName());
        user.setLastName(userUpdateRequest.getLastName());

        keycloak.realm(realm).users().get(user.getId()).update(user);
        return new ResponseEntity<>(modelMapper.map(user, UserResponse.class), HttpStatus.OK);
    }

    private static UserRepresentation getKcUser(UserCreateRequest userCreateRequest) {
        UserRepresentation kcUser = new UserRepresentation();
        kcUser.setUsername(userCreateRequest.getUsername());
        kcUser.setEmail(userCreateRequest.getEmail());
        kcUser.setFirstName(userCreateRequest.getFirstName());
        kcUser.setLastName(userCreateRequest.getLastName());
        kcUser.setEnabled(true);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(userCreateRequest.getPassword());
        kcUser.setCredentials(Collections.singletonList(credential));
        return kcUser;
    }

    private UserRepresentation getKcUserByUsername(String username) {
        return keycloak.realm(realm).users()
                .search(username, true).stream()
                .filter(u -> username.equalsIgnoreCase(u.getUsername()))
                .findFirst()
                .orElse(null);
    }

}
