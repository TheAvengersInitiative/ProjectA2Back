package com.a2.backend.controller;

import com.a2.backend.entity.ApplicationUser;
import com.a2.backend.model.ApplicationUserCreateDTO;
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
public class ApplicationUserController {

    private final ApplicationUserService applicationUserService;

    public ApplicationUserController(ApplicationUserService applicationUserService) {
        this.applicationUserService = applicationUserService;
    }

    @PostMapping
    public ResponseEntity<ApplicationUser> postNewUser(
            @Valid @RequestBody ApplicationUserCreateDTO applicationUserCreateDTO) {
        val createdUser = applicationUserService.createUser(applicationUserCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
}
