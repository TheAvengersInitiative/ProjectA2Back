package com.a2.backend.service.impl;

import static java.util.Collections.emptyList;

import com.a2.backend.entity.ApplicationUser;
import com.a2.backend.exception.UserWithThatEmailExistsException;
import com.a2.backend.exception.UserWithThatNicknameExistsException;
import com.a2.backend.model.UserCreateDTO;
import com.a2.backend.repository.ApplicationUserRepository;
import com.a2.backend.service.ApplicationUserService;
import java.util.Optional;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ApplicationUserServiceImpl implements ApplicationUserService, UserDetailsService {

    private final ApplicationUserRepository applicationUserRepository;

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
                            userCreateDTO.getNickname()));
        ApplicationUser user =
                ApplicationUser.builder()
                        .nickname(userCreateDTO.getNickname())
                        .email(userCreateDTO.getEmail())
                        .biography(userCreateDTO.getBiography())
                        .password(new BCryptPasswordEncoder().encode(userCreateDTO.getPassword()))
                        .build();
        return applicationUserRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String nickname) throws UsernameNotFoundException {
        Optional<ApplicationUser> applicationUser =
                applicationUserRepository.findByNickname(nickname);
        if (!applicationUser.isPresent()) {
            throw new UsernameNotFoundException(nickname);
        }
        return new User(
                applicationUser.get().getNickname(),
                applicationUser.get().getPassword(),
                emptyList());
    }
}
