package com.devops.kruschefan.user.controller;

import com.devops.kruschefan.openapi.api.UserApi;
import com.devops.kruschefan.openapi.api.UserApiDelegate;
import com.devops.kruschefan.user.dto.CreateUserDto;
import com.devops.kruschefan.user.dto.UserDto;
import com.devops.kruschefan.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {
    private final UserService userService;

    @Override
    public UserApiDelegate getDelegate() {
        return userService;
    }
}
