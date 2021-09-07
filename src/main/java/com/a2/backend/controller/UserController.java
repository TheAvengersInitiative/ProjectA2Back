package com.a2.backend.controller;

import com.a2.backend.constants.SecurityConstants;
import com.a2.backend.entity.User;
import com.a2.backend.model.UserCreateDTO;
import com.a2.backend.service.UserService;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> postNewUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        val createdUser = userService.createUser(userCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/confirm/{token}/{id}")
    public ResponseEntity<User> confirmUser(@PathVariable String token, @PathVariable UUID id) {
        val userConfirmed = userService.confirmUser(token, id);
        return ResponseEntity.status(HttpStatus.OK).body(userConfirmed);
    }

    @Secured({SecurityConstants.USER_ROLE})
    @DeleteMapping
    public ResponseEntity<?> deleteUser() {
        userService.deleteUser();
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
