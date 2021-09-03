package com.a2.backend.service;

import com.a2.backend.entity.ApplicationUser;
import com.a2.backend.model.UserCreateDTO;

public interface ApplicationUserService {

    ApplicationUser createUser(UserCreateDTO userCreateDTO);
}
