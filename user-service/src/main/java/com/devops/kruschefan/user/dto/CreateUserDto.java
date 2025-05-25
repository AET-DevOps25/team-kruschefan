package com.devops.kruschefan.user.dto;

public record CreateUserDto(
        String username,
        String email,
        String password,
        String firstName,
        String lastName
) {}
