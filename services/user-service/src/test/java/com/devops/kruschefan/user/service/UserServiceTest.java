package com.devops.kruschefan.user.service;

import com.devops.kruschefan.user.config.ModelMapperConfig;
import com.devops.kruschefan.user.metrics.ActiveUserGauge;
import com.devops.kruschefan.user.metrics.LoginMetrics;
import com.devops.kruschefan.user.metrics.PayloadMetrics;
import com.devops.kruschefan.user.metrics.ProcessingMetrics;
import com.devops.kruschefan.openapi.model.UserResponse;

import com.devops.kruschefan.openapi.model.UserUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock private Keycloak keycloak;
    @Mock private LoginMetrics loginMetrics;
    @Mock private ProcessingMetrics processingMetrics;
    @Mock private ActiveUserGauge activeUserGauge;
    @Mock private PayloadMetrics payloadMetrics;
    @Mock private ModelMapper modelMapper;

    @InjectMocks
    private UserService userService;

    @Mock private RealmResource realmResource;
    @Mock private UsersResource usersResource;

    @BeforeEach
    public void setup() throws Exception {
        when(keycloak.realm("kruschefan")).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);

        // Inject value into private field `realm` via reflection
        Field realmField = UserService.class.getDeclaredField("realm");
        realmField.setAccessible(true);
        realmField.set(userService, "kruschefan");

        modelMapper = new ModelMapperConfig().modelMapper();
        Field modelMapperField = UserService.class.getDeclaredField("modelMapper");
        modelMapperField.setAccessible(true);
        modelMapperField.set(userService, modelMapper);
    }

    // Tests that getUserByUsername returns the correct user data
    @Test
    public void testGetUserByUsernameReturnsCorrectDto() {
        UserRepresentation user = new UserRepresentation();
        user.setId("abc123");
        user.setUsername("fabio");
        user.setEmail("fabio@example.com");

        when(usersResource.search("fabio", true)).thenReturn(List.of(user));

        UserResponse response = userService.getUser("fabio").getBody();

        assertEquals("fabio", response.getUsername());
        assertEquals("fabio@example.com", response.getEmail());
        assertEquals("abc123", response.getId());
    }

    @Test
    public void testGetAllUsersReturnsCorrectList() {
        UserRepresentation user1 = new UserRepresentation();
        user1.setId("id1");
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");

        UserRepresentation user2 = new UserRepresentation();
        user2.setId("id2");
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");

        when(usersResource.list()).thenReturn(List.of(user1, user2));

        List<UserResponse> response = userService.getAllUsers().getBody();

        assertEquals(2, response.size());
        assertEquals("user1", response.get(0).getUsername());
        assertEquals("user2", response.get(1).getUsername());
    }

    @Test
    public void testDeleteUserByUsernameSuccess() {
        UserRepresentation user = new UserRepresentation();
        user.setId("abc123");
        user.setUsername("fabio");

        UserResource mockUserResource = mock(UserResource.class);

        when(usersResource.search("fabio", true)).thenReturn(List.of(user));
        when(usersResource.get("abc123")).thenReturn(mockUserResource);
        doNothing().when(mockUserResource).remove();

        userService.deleteUser("fabio");

        verify(mockUserResource, times(1)).remove();
        verify(activeUserGauge, times(1)).decrement();
    }


    @Test
    public void testGetUserByUsernameThrowsWhenNotFound() {
        when(usersResource.search("unknown", true)).thenReturn(Collections.emptyList());

        assertThrows(ResponseStatusException.class, () -> {
            userService.getUser("unknown");
        });
    }

    @Test
    public void testUpdateUserByUsernameUpdatesFields() {
        UserRepresentation user = new UserRepresentation();
        user.setId("id123");
        user.setUsername("fabio");
        user.setEmail("old@example.com");

        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setUsername("fabio");
        userUpdateRequest.setEmail("new@example.com");
        userUpdateRequest.setFirstName("Fab");
        userUpdateRequest.setLastName("Io");

        UserResource mockUserResource = mock(UserResource.class);

        when(usersResource.search("fabio", true)).thenReturn(List.of(user));
        when(usersResource.get("id123")).thenReturn(mockUserResource);
        doNothing().when(mockUserResource).update(any(UserRepresentation.class));

        UserResponse response = userService.updateUser("fabio", userUpdateRequest).getBody();

        assertEquals("id123", response.getId());
        assertEquals("fabio", response.getUsername());
        assertEquals("new@example.com", response.getEmail());
        verify(mockUserResource, times(1)).update(any(UserRepresentation.class));
    }
}
