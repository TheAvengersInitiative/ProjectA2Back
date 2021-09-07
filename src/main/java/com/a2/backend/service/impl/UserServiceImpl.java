package com.a2.backend.service.impl;

import com.a2.backend.entity.User;
import com.a2.backend.exception.UserNotFoundException;
import com.a2.backend.exception.TokenConfirmationFailedException;
import com.a2.backend.exception.UserWithThatEmailExistsException;
import com.a2.backend.exception.UserWithThatNicknameExistsException;
import com.a2.backend.model.UserCreateDTO;
import com.a2.backend.repository.UserRepository;
import com.a2.backend.service.UserService;
import java.util.UUID;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
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
                        .confirmationToken(userCreateDTO.getConfirmationToken())
                        .build();
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id))
            throw new UserNotFoundException(String.format("No user found for id: %s", id));
        userRepository.deleteById(id);
    }

    @Override
    public User confirmUser(String token, UUID userID) {
        val userOptional = userRepository.findById(userID);
        if (userOptional.isEmpty()) {
            throw new TokenConfirmationFailedException(
                    String.format("User with id: %s Not Found ", userID));
        }
        val user = userOptional.get();
        if (user.isActive()) {
            throw new TokenConfirmationFailedException(
                    String.format("User %s Already Active ", userID));
        }
        if (!user.getConfirmationToken().equals(token)) {
            throw new TokenConfirmationFailedException(String.format("Invalid Token %s", token));
        }
        user.setActive(true);
        return userRepository.save(user);
    }
}
