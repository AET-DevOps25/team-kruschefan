package com.devops.kruschefan.user.service;

import com.devops.kruschefan.config.KeycloakConfig;
import com.devops.kruschefan.user.dto.CreateUserDto;
import com.devops.kruschefan.user.dto.UserDto;
import com.devops.kruschefan.user.model.User;
import com.devops.kruschefan.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Collections;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Import(KeycloakConfig.class)
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final Keycloak keycloak;

    @Value("${keycloak.realm:${KEYCLOAK_REALM}}")
    private String realm;

    // Create a new user in Keycloak and optionally in the local DB
    public UserDto createUser(CreateUserDto createUserDto) {
        log.info("Creating user in Keycloak: {}", createUserDto.username());

        // Create Keycloak user representation
        UserRepresentation kcUser = new UserRepresentation();
        kcUser.setUsername(createUserDto.username());
        kcUser.setEmail(createUserDto.email());
        kcUser.setFirstName(createUserDto.firstName());
        kcUser.setLastName(createUserDto.lastName());
        kcUser.setEnabled(true);

        // Set password
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(createUserDto.password());
        kcUser.setCredentials(Collections.singletonList(credential));

        // Send to Keycloak
        Response response = keycloak.realm(realm).users().create(kcUser);
        if (response.getStatus() != 201) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Failed to create user in Keycloak. Status: " + response.getStatus()
            );
        }

        String keycloakId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
        log.info("User created in Keycloak with ID: {}", keycloakId);

        // TODO: Add user to group "student" 
        /* 
        List<GroupRepresentation> groups = keycloak.realm(realm).groups().groups();
        GroupRepresentation studentGroup = groups.stream()
                .filter(g -> g.getName().equalsIgnoreCase("student"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Group 'student' not found in Keycloak"));

        keycloak.realm(realm).users().get(keycloakId).joinGroup(studentGroup.getId());
        log.info("User {} added to group 'student'", createUserDto.username());
        */

        return new UserDto(keycloakId, createUserDto.username(), createUserDto.email());
    }

    // Get all users from the local DB
    public List<UserDto> getAllUsers() {
        log.info("Fetching all users from DB");
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(u -> new UserDto(u.getId(), u.getUsername(), u.getEmail()))
                .toList();
    }

    // Get a single user by username
    public UserDto getUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(u -> new UserDto(u.getId(), u.getUsername(), u.getEmail()))
                   .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "User not found: " + username
                    ));
    }

    // Delete user by username
    public void deleteUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "User not found: " + username
            ));

        log.info("Deleting user from Keycloak: {}", username);
        keycloak.realm(realm).users().delete(user.getKeycloakId());

        log.info("Deleting user from DB: {}", username);
        userRepository.delete(user);
    }

    // Update user by username
    public UserDto updateUserByUsername(String username, CreateUserDto dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found: " + username
                ));

        try {
            // Update in Keycloak
            UserRepresentation rep = keycloak.realm(realm).users()
                    .get(user.getKeycloakId()).toRepresentation();

            rep.setFirstName(dto.firstName());
            rep.setLastName(dto.lastName());
            rep.setEmail(dto.email());

            keycloak.realm(realm).users().get(user.getKeycloakId()).update(rep);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to update user in Keycloak: " + e.getMessage()
            );
        }

        // Update local DB
        user.setEmail(dto.email());
        userRepository.save(user);

        return new UserDto(user.getKeycloakId(), user.getUsername(), user.getEmail());
    }


}
