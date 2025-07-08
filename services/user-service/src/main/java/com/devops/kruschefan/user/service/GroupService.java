package com.devops.kruschefan.user.service;

import com.devops.kruschefan.user.config.KeycloakConfig;
import com.devops.kruschefan.openapi.model.UserResponse;
import com.devops.kruschefan.user.dto.GroupDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@Service
@RequiredArgsConstructor
@Import(KeycloakConfig.class)
@Slf4j
public class GroupService {

    private final Keycloak keycloak;
    private final ModelMapper modelMapper;

    @Value("${keycloak.realm:${KEYCLOAK_REALM}}")
    private String realm;

    /**
     * Returns all groups from the configured Keycloak realm.
     */
    public List<GroupDto> getAllGroups() {
        return keycloak.realm(realm).groups().groups().stream()
                .map(group -> new GroupDto(group.getId(), group.getName()))
                .toList();
    }

    /**
     * Returns all users in the specified group by groupId.
     */
    public ResponseEntity<List<UserResponse>> getUsersInGroup(String groupName) {
        log.info("Looking up group '{}'", groupName);

        GroupDto groupDto = keycloak.realm(realm).groups().groups().stream()
                .filter(group -> group.getName().equalsIgnoreCase(groupName))
                .findFirst()
                .map(group -> new GroupDto(group.getId(), group.getName()))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Group '" + groupName + "' not found"
                ));


        return new ResponseEntity<>(keycloak.realm(realm).groups().group(groupDto.id()).members().stream()
                .map(user -> modelMapper.map(user, UserResponse.class))
                .toList(), HttpStatus.OK);
    }
}
