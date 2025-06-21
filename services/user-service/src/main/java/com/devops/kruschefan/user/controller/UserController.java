package com.devops.kruschefan.user.controller;

import com.devops.kruschefan.user.dto.CreateUserDto;
import com.devops.kruschefan.user.dto.UserDto;
import com.devops.kruschefan.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody CreateUserDto createUserDto) {
        return userService.createUser(createUserDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }
    
    // Get a single user by username
    @GetMapping("/{username}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    // Delete a user by username
    @DeleteMapping("/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String username) {
        userService.deleteUserByUsername(username);
    }

    // Update user by username
    @PutMapping("/{username}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto updateUser(@PathVariable String username, @RequestBody CreateUserDto updatedUser) {
        return userService.updateUserByUsername(username, updatedUser);
    }

}
