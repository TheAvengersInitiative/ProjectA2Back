package com.a2.backend.service;

import com.a2.backend.entity.User;
import com.a2.backend.model.UserCreateDTO;
import java.util.UUID;

import java.util.UUID;

public interface UserService {

    User createUser(UserCreateDTO userCreateDTO);

    User confirmUser(String token, UUID userid);

    void deleteUser(UUID id);
}
