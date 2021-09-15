package com.a2.backend.service.impl;

import com.a2.backend.constants.SecurityConstants;
import com.a2.backend.entity.User;
import com.a2.backend.exception.UserIsNotActiveException;
import com.a2.backend.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
        Optional<User> user = userRepository.findByEmail(email);
        if (!user.isPresent()) {
            throw new UsernameNotFoundException(email);
        }
        if (!user.get().isActive()) {
            throw new UsernameNotFoundException(email);
        }
        return new org.springframework.security.core.userdetails.User(
                user.get().getEmail(),
                user.get().getPassword(),
                List.of(new SimpleGrantedAuthority(SecurityConstants.USER_ROLE)));
    }
}
