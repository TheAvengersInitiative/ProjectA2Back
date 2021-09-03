package com.a2.backend.service.impl;

import com.a2.backend.entity.ApplicationUser;
import com.a2.backend.exception.UserWithThatEmailExistsException;
import com.a2.backend.exception.UserWithThatNicknameExistsException;
import com.a2.backend.model.UserCreateDTO;
import com.a2.backend.repository.ApplicationUserRepository;
import com.a2.backend.service.ApplicationUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ApplicationUserServiceImpl implements ApplicationUserService {

    private final ApplicationUserRepository applicationUserRepository;

    @Autowired private PasswordEncoder passwordEncoder;

    public ApplicationUserServiceImpl(ApplicationUserRepository applicationUserRepository) {
        this.applicationUserRepository = applicationUserRepository;
    }

    @Override
    public ApplicationUser createUser(UserCreateDTO userCreateDTO) {
        if (applicationUserRepository.findByNickname(userCreateDTO.getNickname()).isPresent())
            throw new UserWithThatNicknameExistsException(
                    String.format(
                            "There is an existing user the nickname %s",
                            userCreateDTO.getNickname()));
        if (applicationUserRepository.findByEmail(userCreateDTO.getEmail()).isPresent())
            throw new UserWithThatEmailExistsException(
                    String.format(
                            "There is an existing user with the email %s",
                            userCreateDTO.getEmail()));
        ApplicationUser user =
                ApplicationUser.builder()
                        .nickname(userCreateDTO.getNickname())
                        .email(userCreateDTO.getEmail())
                        .biography(userCreateDTO.getBiography())
                        .password(passwordEncoder.encode(userCreateDTO.getPassword()))
                        .build();
        return applicationUserRepository.save(user);
    }
}
