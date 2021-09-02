package com.a2.backend.service.impl;

import static java.util.Collections.emptyList;

import com.a2.backend.entity.User;
import com.a2.backend.exception.UserIsNotActiveException;
import com.a2.backend.repository.UserRepository;
import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ApplicationUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public ApplicationUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException, UserIsNotActiveException {
        Optional<User> applicationUser = userRepository.findByEmail(email);
        if (!applicationUser.isPresent()) {
            throw new UsernameNotFoundException("User was not found");
        }

        return new org.springframework.security.core.userdetails.User(
                applicationUser.get().getEmail(), applicationUser.get().getPassword(), emptyList());
    }
}
