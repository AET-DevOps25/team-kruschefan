package com.devops.kruschefan.user.controller;

import com.devops.kruschefan.openapi.model.UserResponse;
import com.devops.kruschefan.user.dto.GroupDto;
import com.devops.kruschefan.user.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    // Get all Keycloak groups
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<GroupDto> getAllGroups() {
        return groupService.getAllGroups();
    }

    @GetMapping("/name/{groupName}/users")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<UserResponse>> getUsersInGroupByName(@PathVariable String groupName) {
        return groupService.getUsersInGroup(groupName);
    }

}
