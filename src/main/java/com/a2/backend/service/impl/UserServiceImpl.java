package com.a2.backend.service.impl;

import com.a2.backend.constants.PrivacyConstant;
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
import java.util.function.Function;
import java.util.stream.Collectors;
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
    public User updatePrivacySettings(UserPrivacyDTO userPrivacyDTO) {
        User loggedUser = getLoggedUser();

        loggedUser.setOwnedProjectsPrivacy(userPrivacyDTO.getOwnedProjectsPrivacy());
        loggedUser.setCollaboratedProjectsPrivacy(userPrivacyDTO.getCollaboratedProjectsPrivacy());
        loggedUser.setTagsPrivacy(userPrivacyDTO.getTagsPrivacy());
        loggedUser.setLanguagesPrivacy(userPrivacyDTO.getLanguagesPrivacy());

        return userRepository.save(loggedUser);
    }

    @Override
    public UserProfileDTO getUserProfile(UUID id) {
        Optional<User> loggedUserOptional = getUser();
        if (loggedUserOptional.isPresent() && loggedUserOptional.get().getId().equals(id)) {
            User loggedUser = loggedUserOptional.get();
            return UserProfileDTO.builder()
                    .nickname(loggedUser.getNickname())
                    .biography(loggedUser.getBiography())
                    .preferredTags(loggedUser.getPreferredTags())
                    .preferredLanguages(loggedUser.getPreferredLanguages())
                    .ownedProjects(projectService.getProjectsByOwner(loggedUser))
                    .collaboratedProjects(projectService.getCollaboratingProjects(loggedUser))
                    .reputation(loggedUser.getReputation())
                    .build();
        }

        if (userRepository.findById(id).isEmpty())
            throw new UserNotFoundException(String.format("There is no user with id %s", id));

        User user = userRepository.getById(id);

        val userProfile =
                UserProfileDTO.builder()
                        .nickname(user.getNickname())
                        .biography(user.getBiography())
                        .reputation(user.getReputation())
                        .build();

        if (user.getTagsPrivacy().equals(PrivacyConstant.PUBLIC))
            userProfile.setPreferredTags(user.getPreferredTags());

        if (user.getLanguagesPrivacy().equals(PrivacyConstant.PUBLIC))
            userProfile.setPreferredLanguages(user.getPreferredLanguages());

        if (user.getOwnedProjectsPrivacy().equals(PrivacyConstant.PUBLIC))
            userProfile.setOwnedProjects(projectService.getProjectsByOwner(user));

        if (user.getCollaboratedProjectsPrivacy().equals(PrivacyConstant.PUBLIC))
            userProfile.setCollaboratedProjects(projectService.getCollaboratingProjects(user));

        return userProfile;
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

    @Override
    public List<ProjectDTO> getPreferredProjects() {

        Function<List<ProjectDTO>, Optional<ProjectDTO>> getRandomProject =
                projects -> {
                    if (projects.isEmpty()) {
                        return Optional.empty();
                    } else {
                        return Optional.of(projects.remove(new Random().nextInt(projects.size())));
                    }
                };

        List<ProjectDTO> projects = new ArrayList<>();
        List<ProjectDTO> featured = projectService.getFeaturedProject();

        for (int i = 0; i < 2; i++) {
            getRandomProject.apply(featured).map(projects::add);
        }

        List<ProjectDTO> preferredProjects =
                getUser()
                        .map(
                                user -> {
                                    ProjectSearchDTO projectSearchDTO =
                                            ProjectSearchDTO.builder()
                                                    .tags(user.getPreferredTags())
                                                    .languages(user.getPreferredLanguages())
                                                    .build();
                                    return projectService
                                            .searchProjectsByFilter(projectSearchDTO)
                                            .getProjects();
                                })
                        .orElseGet(projectService::getAllProjects);

        for (int i = 0; i < 4; i++) {
            getRandomProject.apply(preferredProjects).map(projects::add);
        }

        val filteredProjects =
                projectService.getAllProjects().stream()
                        .filter(p -> !projects.contains(p))
                        .collect(Collectors.toList());

        while (projects.size() < 6 && !filteredProjects.isEmpty()) {
            getRandomProject.apply(filteredProjects).map(projects::add);
        }
        return new ArrayList<>(projects);
    }

    @Override
    public Optional<User> getUser() {
        return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findByEmail);
    }

    @Override
    public User getUser(UUID id) {
        return userRepository.getById(id);
    }

    @Override
    public User updateReputation(UUID id) {
        List<ReviewDTO> latestReviewForEveryProject = new ArrayList<>();

        User user = userRepository.getById(id);
        List<ProjectDTO> collaboratingProjects = projectService.getCollaboratingProjects(user);

        for (ProjectDTO collaboratingProject : collaboratingProjects) {
            List<ReviewDTO> reviewsForProject =
                    collaboratingProject.getReviews().stream()
                            .filter(r -> r.getCollaborator().getId().equals(id))
                            .collect(Collectors.toList());
            if (reviewsForProject.isEmpty()) continue;
            if (reviewsForProject.size() > 1) {
                reviewsForProject.sort(Comparator.comparing(ReviewDTO::getDate));
                Collections.reverse(reviewsForProject);
            }
            latestReviewForEveryProject.add(reviewsForProject.get(0));
        }

        double reputation =
                latestReviewForEveryProject.stream()
                        .mapToDouble(ReviewDTO::getScore)
                        .average()
                        .orElse(0);

        user.setReputation(reputation);
        return userRepository.save(user);
    }

    @Override
    public List<ReviewDTO> getUserReviews(UUID id) {
        List<ReviewDTO> reviews = new ArrayList<>();

        val user =
                userRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new UserNotFoundException(
                                                String.format("User with id %s not found ", id)));
        List<ProjectDTO> collaboratingProjects = projectService.getCollaboratingProjects(user);

        for (ProjectDTO collaboratingProject : collaboratingProjects) {
            List<ReviewDTO> reviewsForProject =
                    collaboratingProject.getReviews().stream()
                            .filter(r -> r.getCollaborator().getId().equals(id))
                            .collect(Collectors.toList());
            reviews.addAll(reviewsForProject);
        }

        reviews.sort(Comparator.comparing(ReviewDTO::getDate));
        Collections.reverse(reviews);

        return reviews;
    }

    @Override
    public boolean switchEmailNotificationPreferences(
            NotificationUpdatePreferencDTO notificationUpdatePreferencDTO) {
        User loggedUser = getLoggedUser();
        loggedUser.setAllowsNotifications(notificationUpdatePreferencDTO.isAllowsNotifications());
        userRepository.save(loggedUser);
        return loggedUser.isAllowsNotifications();
    }
}
