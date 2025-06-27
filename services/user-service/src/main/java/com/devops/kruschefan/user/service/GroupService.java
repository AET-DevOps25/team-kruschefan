package com.devops.kruschefan.user.service;

import com.devops.kruschefan.config.KeycloakConfig;
import com.devops.kruschefan.user.dto.GroupDto;
import com.devops.kruschefan.user.dto.UserDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
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

    @Value("${keycloak.realm:${KEYCLOAK_REALM}}")
    private String realm;

    /**
     * Returns all groups from the configured Keycloak realm.
     */
    public List<GroupDto> getAllGroups() {
        return keycloak.realm(realm).groups().groups().stream()
            .map(gr -> new GroupDto(gr.getId(), gr.getName()))
            .toList();
    }

    /**
     * Returns all users in the specified group by groupId.
     */
    public List<UserDto> getUsersInGroup(String groupName) {
        log.info("Looking up group '{}'", groupName);
        
        GroupDto groupDto = keycloak.realm(realm).groups().groups().stream()
            .filter(g -> g.getName().equalsIgnoreCase(groupName))
            .findFirst()
            .map(g -> new GroupDto(g.getId(), g.getName()))
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Group '" + groupName + "' not found"
            ));

        
        return keycloak.realm(realm).groups().group(groupDto.id()).members().stream()
            .map(u -> new UserDto(u.getId(), u.getUsername(), u.getEmail()))
            .toList();
    }
}
