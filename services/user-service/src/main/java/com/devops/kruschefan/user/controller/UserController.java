package com.devops.kruschefan.user.controller;

import com.devops.kruschefan.openapi.api.UserApi;
import com.devops.kruschefan.openapi.model.UserCreateRequest;
import com.devops.kruschefan.openapi.model.UserResponse;
import com.devops.kruschefan.openapi.model.UserUpdateRequest;
import com.devops.kruschefan.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('client_user')")
public class UserController implements UserApi {

    private final UserService userService;

    @Override
    public ResponseEntity<UserResponse> createUser(UserCreateRequest userCreateRequest) {
        return userService.createUser(userCreateRequest);
    }

    @Override
    public ResponseEntity<Void> deleteUser(String username) {
        return userService.deleteUser(username);
    }

    @Override
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return userService.getAllUsers();
    }

    @Override
    public ResponseEntity<UserResponse> getUser(String username) {
        return userService.getUser(username);
    }

    @Override
    public ResponseEntity<UserResponse> updateUser(String username, UserUpdateRequest userUpdateRequest) {
        return userService.updateUser(username, userUpdateRequest);
    }
}
