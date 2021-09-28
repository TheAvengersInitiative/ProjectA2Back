package com.a2.backend.service.impl;

import com.a2.backend.entity.User;
import com.a2.backend.exception.*;
import com.a2.backend.model.*;
import com.a2.backend.repository.UserRepository;
import com.a2.backend.service.MailService;
import com.a2.backend.service.ProjectService;
import com.a2.backend.service.UserService;
import com.a2.backend.utils.RandomStringUtils;
import com.a2.backend.utils.SecurityUtils;
import java.util.*;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final ProjectService projectService;

    private final MailService mailService;

    @Autowired private PasswordEncoder passwordEncoder;

    private final String validLanguageNames =
            "Java, C, C++, C#, Python, Visual Basic .NET, PHP, JavaScript, TypeScript, Delphi/Object Pascal, Swift, Perl, Ruby, Assembly language, R, Visual Basic, Objective-C, Go, MATLAB, PL/SQL, Scratch, SAS, D, Dart, ABAP, COBOL, Ada, Fortran, Transact-SQL, Lua, Scala, Logo, F#, Lisp, LabVIEW, Prolog, Haskell, Scheme, Groovy, RPG (OS/400), Apex, Erlang, MQL4, Rust, Bash, Ladder Logic, Q, Julia, Alice, VHDL, Awk, (Visual) FoxPro, ABC, ActionScript, APL, AutoLISP, bc, BlitzMax, Bourne shell, C shell, CFML, cg, CL (OS/400), Clipper, Clojure, Common Lisp, Crystal, Eiffel, Elixir, Elm, Emacs Lisp, Forth, Hack, Icon, IDL, Inform, Io, J, Korn shell, Kotlin, Maple, ML, NATURAL, NXT-G, OCaml, OpenCL, OpenEdge ABL, Oz, PL/I, PowerShell, REXX, Ring, S, Smalltalk, SPARK, SPSS, Standard ML, Stata, Tcl, VBScript, Verilog";

    private final List<String> validLanguageList =
            new ArrayList<>(Arrays.asList(validLanguageNames.split(", ")));

    public UserServiceImpl(
            UserRepository userRepository,
            @Lazy ProjectService projectService,
            MailService mailService) {
        this.userRepository = userRepository;
        this.projectService = projectService;
        this.mailService = mailService;
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
        String randomStringUtils = RandomStringUtils.getAlphaNumericString(32);
        User user =
                User.builder()
                        .nickname(userCreateDTO.getNickname())
                        .email(userCreateDTO.getEmail().toLowerCase())
                        .biography(userCreateDTO.getBiography())
                        .password(passwordEncoder.encode(userCreateDTO.getPassword()))
                        .confirmationToken(randomStringUtils)
                        .passwordRecoveryToken(randomStringUtils)
                        .build();
        User savedUser = userRepository.save(user);
        mailService.sendConfirmationMail(savedUser);
        return savedUser;
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
        if (userUpdateDTO.getPassword() != null)
            loggedUser.setPassword(passwordEncoder.encode(userUpdateDTO.getPassword()));
        return userRepository.save(loggedUser);
    }

    @Override
    public User updatePreferences(PreferencesUpdateDTO preferencesUpdateDTO) {
        User loggedUser = getLoggedUser();

        for (String language : preferencesUpdateDTO.getLanguages()) {
            if (!validLanguageList.contains(language))
                throw new LanguageNotValidException(
                        String.format("Language %s is not valid", language));
        }

        loggedUser.setPreferredLanguages(preferencesUpdateDTO.getLanguages());
        loggedUser.setPreferredTags(preferencesUpdateDTO.getTags());

        return userRepository.save(loggedUser);
    }

    @Override
    public User getLoggedUser() {
        String email =
                SecurityUtils.getCurrentUserLogin()
                        .orElseThrow(() -> new UserNotLoggedIn("You must login first"));
        return userRepository
                .findByEmail(email)
                .orElseThrow(
                        () ->
                                new UserNotFoundException(
                                        String.format("No user found for email: %s", email)));
    }

    @Override
    public User confirmUser(ConfirmationTokenDTO confirmationTokenDTO) {
        val user =
                userRepository
                        .findById(confirmationTokenDTO.getId())
                        .orElseThrow(
                                () ->
                                        new TokenConfirmationFailedException(
                                                String.format(
                                                        "User with id %s not found ",
                                                        confirmationTokenDTO.getId())));

        if (user.isActive()) {
            throw new TokenConfirmationFailedException(
                    String.format("User %s Already Active ", user.getId()));
        }
        if (!user.getConfirmationToken().equals(confirmationTokenDTO.getConfirmationToken())) {
            throw new TokenConfirmationFailedException(
                    String.format("Invalid Token %s", confirmationTokenDTO.getConfirmationToken()));
        }
        user.setActive(true);
        return userRepository.save(user);
    }

    @Override
    public User recoverPassword(PasswordRecoveryDTO passwordRecoveryDTO) {
        val user =
                userRepository
                        .findById(passwordRecoveryDTO.getId())
                        .orElseThrow(
                                () ->
                                        new InvalidPasswordRecoveryException(
                                                "Invalid PasswordRecovery"));

        if (!user.isActive()) {
            throw new InvalidPasswordRecoveryException("Invalid PasswordRecovery");
        }
        if (!user.getPasswordRecoveryToken()
                .equals(passwordRecoveryDTO.getPasswordRecoveryToken())) {
            throw new TokenConfirmationFailedException(
                    String.format(
                            "Invalid Token %s", passwordRecoveryDTO.getPasswordRecoveryToken()));
        }

        if (passwordRecoveryDTO.getNewPassword().length() < 8
                || passwordRecoveryDTO.getNewPassword().length() > 32) {
            throw new PasswordRecoveryFailedException("Invalid Body");
        }

        user.setPassword(passwordEncoder.encode(passwordRecoveryDTO.getNewPassword()));
        String randomStringUtils = RandomStringUtils.getAlphaNumericString(32);
        user.setPasswordRecoveryToken(randomStringUtils);

        return userRepository.save(user);
    }

    @Override
    public void sendPasswordRecoveryMail(PasswordRecoveryInitDTO passwordRecoveryInitDTO) {
        val userOptional = userRepository.findByEmail(passwordRecoveryInitDTO.getEmail());
        userOptional.ifPresent(mailService::sendForgotPasswordMail);
    }
}
