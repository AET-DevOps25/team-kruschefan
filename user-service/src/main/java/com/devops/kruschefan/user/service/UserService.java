package com.devops.kruschefan.user.service;

import com.devops.kruschefan.config.KeycloakConfig;
import com.devops.kruschefan.user.dto.CreateUserDto;
import com.devops.kruschefan.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
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
public class UserService {

    private final Keycloak keycloak;

    @Value("${keycloak.realm:${KEYCLOAK_REALM}}")
    private String realm;

    public UserDto createUser(CreateUserDto createUserDto) {
        log.info("Creating user in Keycloak: {}", createUserDto.username());

        UserRepresentation kcUser = new UserRepresentation();
        kcUser.setUsername(createUserDto.username());
        kcUser.setEmail(createUserDto.email());
        kcUser.setFirstName(createUserDto.firstName());
        kcUser.setLastName(createUserDto.lastName());
        kcUser.setEnabled(true);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(createUserDto.password());
        kcUser.setCredentials(Collections.singletonList(credential));

        Response response = keycloak.realm(realm).users().create(kcUser);
        if (response.getStatus() != 201) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Failed to create user in Keycloak. Status: " + response.getStatus()
            );
        }

        String keycloakId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
        return new UserDto(keycloakId, createUserDto.username(), createUserDto.email());
    }

    public List<UserDto> getAllUsers() {
        return keycloak.realm(realm).users().list().stream()
                .map(u -> new UserDto(u.getId(), u.getUsername(), u.getEmail()))
                .collect(Collectors.toList());
    }

    public UserDto getUserByUsername(String username) {
        List<UserRepresentation> users = keycloak.realm(realm).users().search(username, true);
        return users.stream()
                .filter(u -> username.equalsIgnoreCase(u.getUsername()))
                .findFirst()
                .map(u -> new UserDto(u.getId(), u.getUsername(), u.getEmail()))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found: " + username
                ));
    }

    public void deleteUserByUsername(String username) {
        UserRepresentation user = keycloak.realm(realm).users()
                .search(username, true).stream()
                .filter(u -> username.equalsIgnoreCase(u.getUsername()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found: " + username
                ));

        keycloak.realm(realm).users().get(user.getId()).remove();
        log.info("Deleted Keycloak user: {}", username);
    }

    public UserDto updateUserByUsername(String username, CreateUserDto dto) {
        UserRepresentation user = keycloak.realm(realm).users()
                .search(username, true).stream()
                .filter(u -> username.equalsIgnoreCase(u.getUsername()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found: " + username
                ));

        user.setEmail(dto.email());
        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());

        keycloak.realm(realm).users().get(user.getId()).update(user);
        return new UserDto(user.getId(), user.getUsername(), user.getEmail());
    }
}
