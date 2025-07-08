package com.devops.kruschefan.user.service;

import com.devops.kruschefan.openapi.model.UserResponse;
import com.devops.kruschefan.user.config.ModelMapperConfig;
import com.devops.kruschefan.user.dto.GroupDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.admin.client.resource.GroupsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GroupServiceTest {

    @Mock private Keycloak keycloak;
    @Mock private RealmResource realmResource;
    @Mock private GroupsResource groupsResource;
    @Mock private GroupResource groupResource;
    @Mock private ModelMapper modelMapper;

    @InjectMocks
    private GroupService groupService;

    @BeforeEach
    public void setup() throws Exception {
        when(keycloak.realm("kruschefan")).thenReturn(realmResource);
        when(realmResource.groups()).thenReturn(groupsResource);

        Field realmField = GroupService.class.getDeclaredField("realm");
        realmField.setAccessible(true);
        realmField.set(groupService, "kruschefan");

        modelMapper = new ModelMapperConfig().modelMapper();
        Field modelMapperField = GroupService.class.getDeclaredField("modelMapper");
        modelMapperField.setAccessible(true);
        modelMapperField.set(groupService, modelMapper);
    }

    // Test for fetching all groups
    @Test
    public void testGetAllGroupsReturnsList() {
        GroupRepresentation gr = new GroupRepresentation();
        gr.setId("group1");
        gr.setName("Admins");

        when(groupsResource.groups()).thenReturn(List.of(gr));

        List<GroupDto> result = groupService.getAllGroups();

        assertEquals(1, result.size());
        assertEquals("Admins", result.getFirst().name());
    }

    // Test for fetching users in a specific group
    @Test
    public void testGetUsersInGroupReturnsUsers() {
        GroupRepresentation gr = new GroupRepresentation();
        gr.setId("group1");
        gr.setName("Admins");

        UserRepresentation user = new UserRepresentation();
        user.setId("u1");
        user.setUsername("fabio");
        user.setEmail("fabio@example.com");

        when(groupsResource.groups()).thenReturn(List.of(gr));
        when(groupsResource.group("group1")).thenReturn(groupResource);
        when(groupResource.members()).thenReturn(List.of(user));

        List<UserResponse> result = groupService.getUsersInGroup("Admins").getBody();

        assertEquals(1, result.size());
        assertEquals("fabio", result.getFirst().getUsername());
    }

    // Test for group not found
    @Test
    public void testGetUsersInGroupThrowsIfGroupNotFound() {
        when(groupsResource.groups()).thenReturn(List.of());

        assertThrows(ResponseStatusException.class, () -> {
            groupService.getUsersInGroup("UnknownGroup");
        });
    }
}
