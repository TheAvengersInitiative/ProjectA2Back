package com.a2.backend.controller;

import com.a2.backend.constants.SecurityConstants;
import com.a2.backend.entity.User;
import com.a2.backend.model.*;
import com.a2.backend.service.UserService;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
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

    @Secured({SecurityConstants.USER_ROLE})
    @GetMapping
    public ResponseEntity<User> getLoggedUser() {
        val loggedUser = userService.getLoggedUser();
        return ResponseEntity.status(HttpStatus.OK).body(loggedUser);
    }

    @PostMapping("/confirm")
    public ResponseEntity<User> confirmUser(
            @Valid @RequestBody ConfirmationTokenDTO confirmationTokenDTO) {
        val userConfirmed = userService.confirmUser(confirmationTokenDTO);
        return ResponseEntity.status(HttpStatus.OK).body(userConfirmed);
    }

    @PostMapping("/recover")
    public ResponseEntity<?> passwordRecoveryInit(
            @RequestBody PasswordRecoveryInitDTO passwordRecoveryInitDTO) {
        userService.sendPasswordRecoveryMail(passwordRecoveryInitDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/recover/request")
    public ResponseEntity<User> recoverPassword(
            @RequestBody PasswordRecoveryDTO passwordRecoveryDTO) {
        val userTobeUpdated = userService.recoverPassword(passwordRecoveryDTO);
        return ResponseEntity.status(HttpStatus.OK).body(userTobeUpdated);
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
        val updatedUser = userService.updateUser(userUpdateDTO);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }

    @Secured({SecurityConstants.USER_ROLE})
    @PutMapping("/preferences")
    public ResponseEntity<?> updatePreferences(
            @Valid @RequestBody PreferencesUpdateDTO preferencesUpdateDTO) {
        val updatedUser = userService.updatePreferences(preferencesUpdateDTO);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }

    @Secured({SecurityConstants.USER_ROLE})
    @PutMapping("/privacy")
    public ResponseEntity<?> updatePrivacySettings(
            @Valid @RequestBody UserPrivacyDTO userPrivacyDTO) {
        val updatedUser = userService.updatePrivacySettings(userPrivacyDTO);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }

    @GetMapping("/preferences")
    public ResponseEntity<List<ProjectDTO>> getPreferredProjects() {
        val preferredProjects = userService.getPreferredProjects();
        return ResponseEntity.status(HttpStatus.OK).body(preferredProjects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserProfile(@PathVariable UUID id) {
        val userProfile = userService.getUserProfile(id);
        return ResponseEntity.status(HttpStatus.OK).body(userProfile);
    }
}
