package com.jwt.auth.controller;

import com.jwt.auth.dto.response.UserDto;
import com.jwt.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/admin",
        produces = "application/json")
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "/users")
    public ResponseEntity<Iterable<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PatchMapping(path = "/users/{email}/assign-role")
    public ResponseEntity<UserDto> assignRoleToUser(
            @PathVariable("email") String email,
            @RequestParam String roleName) {
        return ResponseEntity.ok(userService.assignRoleToUser(email, roleName));
    }

    @PatchMapping(path = "/users/{email}/disable")
    public ResponseEntity<UserDto> disableUser(
            @PathVariable("email") String email) {
        return ResponseEntity.ok(userService.disableUser(email));
    }
}
