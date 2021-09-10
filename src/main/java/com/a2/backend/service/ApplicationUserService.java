package com.a2.backend.service;

import com.a2.backend.entity.ApplicationUser;
import com.a2.backend.model.ApplicationUserCreateDTO;

public interface ApplicationUserService {

    ApplicationUser createUser(ApplicationUserCreateDTO applicationUserCreateDTO);
}
