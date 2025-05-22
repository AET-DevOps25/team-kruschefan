package com.devops.kruschefan.user.service;

import com.devops.kruschefan.user.dto.CreateUserDto;
import com.devops.kruschefan.user.dto.UserDto;
import com.devops.kruschefan.user.model.User;
import com.devops.kruschefan.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    public UserDto createUser(CreateUserDto createUserDto) {
        log.info("Creating user with username: {}", createUserDto.username());
        User user = User.builder().username(createUserDto.username()).email(createUserDto.email()).build();
        userRepository.save(user);
        log.info("User created with id: {}", user.getId());
        return new UserDto(user.getId(), user.getUsername(), user.getEmail());
    }

    public List<UserDto> getAllUsers() {
        log.info("Fetching all users");
        List<User> users = userRepository.findAll();
        log.info("Fetched {} users", users.size());
        return users.stream()
                .map(user -> new UserDto(user.getId(), user.getUsername(), user.getEmail()))
                .toList();
    }
}
