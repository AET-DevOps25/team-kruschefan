package com.devops.kruschefan.user.service;

import com.devops.kruschefan.metrics.ActiveUserGauge;
import com.devops.kruschefan.metrics.LoginMetrics;
import com.devops.kruschefan.metrics.PayloadMetrics;
import com.devops.kruschefan.metrics.ProcessingMetrics;
import com.devops.kruschefan.user.dto.UserDto;
import com.devops.kruschefan.user.dto.CreateUserDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock private Keycloak keycloak;
    @Mock private LoginMetrics loginMetrics;
    @Mock private ProcessingMetrics processingMetrics;
    @Mock private ActiveUserGauge activeUserGauge;
    @Mock private PayloadMetrics payloadMetrics;

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
    }

    // Tests that getUserByUsername returns the correct user data
    @Test
    public void testGetUserByUsernameReturnsCorrectDto() {
        UserRepresentation user = new UserRepresentation();
        user.setId("abc123");
        user.setUsername("fabio");
        user.setEmail("fabio@example.com");

        when(usersResource.search("fabio", true)).thenReturn(List.of(user));

        UserDto result = userService.getUserByUsername("fabio");

        assertEquals("fabio", result.username());
        assertEquals("fabio@example.com", result.email());
        assertEquals("abc123", result.id());
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

        List<UserDto> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).username());
        assertEquals("user2", result.get(1).username());
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

        userService.deleteUserByUsername("fabio");

        verify(mockUserResource, times(1)).remove();
        verify(activeUserGauge, times(1)).decrement();
    }


    @Test
    public void testGetUserByUsernameThrowsWhenNotFound() {
        when(usersResource.search("unknown", true)).thenReturn(Collections.emptyList());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userService.getUserByUsername("unknown");
        });

        assertEquals("404 NOT_FOUND \"User not found: unknown\"", exception.getMessage());
    }

    @Test
    public void testUpdateUserByUsernameUpdatesFields() {
        UserRepresentation user = new UserRepresentation();
        user.setId("id123");
        user.setUsername("fabio");
        user.setEmail("old@example.com");

        CreateUserDto updateDto = new CreateUserDto("fabio", "new@example.com", "Fab", "Io", "pwd123");

        UserResource mockUserResource = mock(UserResource.class);

        when(usersResource.search("fabio", true)).thenReturn(List.of(user));
        when(usersResource.get("id123")).thenReturn(mockUserResource);
        doNothing().when(mockUserResource).update(any(UserRepresentation.class));

        UserDto result = userService.updateUserByUsername("fabio", updateDto);

        assertEquals("id123", result.id());
        assertEquals("fabio", result.username());
        assertEquals("new@example.com", result.email());
        verify(mockUserResource, times(1)).update(any(UserRepresentation.class));
    }
}
