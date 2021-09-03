package com.a2.backend.controller;

import com.a2.backend.entity.ApplicationUser;
import com.a2.backend.model.UserCreateDTO;
import com.a2.backend.service.ApplicationUserService;
import javax.validation.Valid;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    private final ApplicationUserService applicationUserService;

    public UserController(ApplicationUserService applicationUserService) {
        this.applicationUserService = applicationUserService;
    }

    @PostMapping
    public ResponseEntity<ApplicationUser> postNewUser(
            @Valid @RequestBody UserCreateDTO userCreateDTO) {
        val createdUser = applicationUserService.createUser(userCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
}
