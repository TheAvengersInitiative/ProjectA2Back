package com.a2.backend.service.impl;

import com.a2.backend.entity.User;
import com.a2.backend.exception.TokenConfirmationFailedException;
import com.a2.backend.exception.UserNotFoundException;
import com.a2.backend.exception.UserWithThatEmailExistsException;
import com.a2.backend.exception.UserWithThatNicknameExistsException;
import com.a2.backend.model.UserCreateDTO;
import com.a2.backend.model.UserUpdateDTO;
import com.a2.backend.repository.UserRepository;
import com.a2.backend.service.ProjectService;
import com.a2.backend.service.UserService;
import com.a2.backend.utils.SecurityUtils;
import java.util.Optional;
import java.util.UUID;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final ProjectService projectService;

    @Autowired private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, ProjectService projectService) {
        this.userRepository = userRepository;
        this.projectService = projectService;
    }

    @Override
    public User createUser(UserCreateDTO userCreateDTO) {
        if (userRepository.findByNickname(userCreateDTO.getNickname()).isPresent())
            throw new UserWithThatNicknameExistsException(
                    String.format(
                            "There is an existing user with the nickname %s",
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
    public void deleteUser() {
        User loggedUser = getLoggedUser();
        projectService.deleteProjectsFromUser(loggedUser);
        userRepository.deleteById(loggedUser.getId());
    }

    @Override
    public User updateUser(UserUpdateDTO userUpdateDTO) {
        User loggedUser = getLoggedUser();

        if (userRepository.findByNickname(userUpdateDTO.getNickname()).isPresent()
                && !loggedUser.getNickname().equals(userUpdateDTO.getNickname())) {
            throw new UserWithThatNicknameExistsException(
                    String.format(
                            "There is an existing user with the nickname %s",
                            userUpdateDTO.getNickname()));
        }

        loggedUser.setNickname(userUpdateDTO.getNickname());
        loggedUser.setBiography(userUpdateDTO.getBiography());
        loggedUser.setPassword(passwordEncoder.encode(userUpdateDTO.getPassword()));
        return userRepository.save(loggedUser);
    }

    private User getLoggedUser() {
        String email = SecurityUtils.getCurrentUserLogin().get();
        Optional<User> loggedUser = userRepository.findByEmail(email);
        if (loggedUser.isEmpty())
            throw new UserNotFoundException(String.format("No user found for email: %s", email));
        return loggedUser.get();
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

    @Override
    public User recoverPassword(String email, String newPassword) {
        val userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return null;
        }
        val userToUpdatePassword = userOptional.get();

        userToUpdatePassword.setPassword(newPassword);

        return userRepository.save(userToUpdatePassword);
    }
}
