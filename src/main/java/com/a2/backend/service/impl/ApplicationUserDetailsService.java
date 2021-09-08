package com.a2.backend.service.impl;

import static java.util.Collections.emptyList;

import com.a2.backend.entity.ApplicationUser;
import com.a2.backend.exception.UserIsNotActiveException;
import com.a2.backend.repository.ApplicationUserRepository;
import java.util.Optional;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ApplicationUserDetailsService implements UserDetailsService {

    private final ApplicationUserRepository applicationUserRepository;

    public ApplicationUserDetailsService(ApplicationUserRepository applicationUserRepository) {
        this.applicationUserRepository = applicationUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException, UserIsNotActiveException {
        Optional<ApplicationUser> applicationUser = applicationUserRepository.findByEmail(email);
        if (!applicationUser.isPresent()) {
            throw new UsernameNotFoundException("User was not found");
        }

        return new User(
                applicationUser.get().getEmail(), applicationUser.get().getPassword(), emptyList());
    }
}
