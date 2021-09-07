package com.a2.backend.service.impl;

import com.a2.backend.entity.User;
import com.a2.backend.exception.UserWithThatEmailExistsException;
import com.a2.backend.exception.UserWithThatNicknameExistsException;
import com.a2.backend.model.UserCreateDTO;
import com.a2.backend.repository.ConfirmationTokenRepository;
import com.a2.backend.repository.UserRepository;
import com.a2.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired private PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserRepository userRepository,
            ConfirmationTokenRepository confirmationTokenRepository) {
        this.userRepository = userRepository;
        this.confirmationTokenRepository = confirmationTokenRepository;
    }

    @Override
    public User createUser(UserCreateDTO userCreateDTO) {
        if (userRepository.findByNickname(userCreateDTO.getNickname()).isPresent())
            throw new UserWithThatNicknameExistsException(
                    String.format(
                            "There is an existing user the nickname %s",
                            userCreateDTO.getNickname()));
        if (userRepository.findByEmail(userCreateDTO.getEmail()).isPresent())
            throw new UserWithThatEmailExistsException(
                    String.format(
                            "There is an existing user with the email %s",
                            userCreateDTO.getEmail()));
        User user =
                User.builder()
                        .nickname(userCreateDTO.getNickname())
                        .email(userCreateDTO.getEmail())
                        .biography(userCreateDTO.getBiography())
                        .password(passwordEncoder.encode(userCreateDTO.getPassword()))
                        .build();
        return userRepository.save(user);
    }

    @Override
    public User confirmUser(String token) {
        return null;
        /*
        buscar si el token existe
        if not, throw new exception : InvalidToken
        user.setisActive = true;
        confirmationToken.delete()

        return userRepository.save();
         */
    }
}
