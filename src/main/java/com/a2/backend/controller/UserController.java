package com.a2.backend.controller;

import com.a2.backend.constants.SecurityConstants;
import com.a2.backend.entity.User;
import com.a2.backend.model.UserCreateDTO;
import com.a2.backend.model.UserUpdateDTO;
import com.a2.backend.service.UserService;
import java.util.UUID;
import javax.validation.Valid;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/confirm/{id}/{token}")
    public ResponseEntity<User> confirmUser(@PathVariable UUID id, @PathVariable String token) {
        val userConfirmed = userService.confirmUser(token, id);
        return ResponseEntity.status(HttpStatus.OK).body(userConfirmed);
    }

    @Secured({SecurityConstants.USER_ROLE})
    @DeleteMapping
    public ResponseEntity<?> deleteUser() {
        userService.deleteUser();
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Secured({SecurityConstants.USER_ROLE})
    @PutMapping
    public ResponseEntity<?> updateUser(@Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        User updatedUser = userService.updateUser(userUpdateDTO);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }
}
