package com.a2.backend.service;

import com.a2.backend.entity.User;
import com.a2.backend.model.UserCreateDTO;

public interface UserService {

    User createUser(UserCreateDTO userCreateDTO);
}
