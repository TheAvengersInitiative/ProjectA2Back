package com.a2.backend.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.a2.backend.AbstractTest;
import com.a2.backend.entity.User;
import com.a2.backend.exception.*;
import com.a2.backend.model.*;
import com.a2.backend.service.ProjectService;
import com.a2.backend.service.UserService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UserServiceImplTest extends AbstractTest {

    @Autowired private UserService userService;

    @Autowired private ProjectService projectService;

    @Autowired private PasswordEncoder passwordEncoder;

    String nickname = "nickname";
    String email = "some@email.com";
    String biography = "bio";
    String password = "password";
    String confirmationToken = "token001";
    String passwordRecoveryToken = "recoveryToken001";
    String newPassword = "newPassword001";

    PasswordRecoveryInitDTO passwordRecoveryInitDTO =
            PasswordRecoveryInitDTO.builder().email(email).build();
    UserCreateDTO userCreateDTO =
            UserCreateDTO.builder()
                    .nickname(nickname)
                    .email(email)
                    .biography(biography)
                    .password(password)
                    .build();

    String updatedNickname = "updated nickname";
    String updatedBiography = "updated bio";
    String updatedPassword = "updated password";
    UserUpdateDTO userUpdateDTO =
            UserUpdateDTO.builder()
                    .nickname(updatedNickname)
                    .biography(updatedBiography)
                    .password(updatedPassword)
                    .build();

    List<String> tags = List.of("tag1", "tag2");
    List<String> languages = List.of("Java", "C");

    @Test
    void
            Test001_GivenAValidUserCreateDTOWhenCreatingUserThenThePersistedUserWithHashedPasswordIsReturned() {
        User persistedApplicationUser = userService.createUser(userCreateDTO);
        assertEquals(nickname, persistedApplicationUser.getNickname());
        assertEquals(email, persistedApplicationUser.getEmail());
        assertEquals(biography, persistedApplicationUser.getBiography());
        assertNotEquals(password, persistedApplicationUser.getPassword());
        assertFalse(persistedApplicationUser.isActive());
    }

    @Test
    void Test002_GivenAUserCreateDTOWithAnExistingNicknameWhenCreatingUserThenExceptionIsThrown() {
        UserCreateDTO nonValidUserCreateDTO =
                UserCreateDTO.builder()
                        .nickname(nickname)
                        .email("another@email.com")
                        .biography("another bio")
                        .password("anotherPassword")
                        .build();

        userService.createUser(userCreateDTO);

        assertThrows(
                UserWithThatNicknameExistsException.class,
                () -> userService.createUser(nonValidUserCreateDTO));
    }

    @Test
    void Test003_GivenAUserCreateDTOWithAnExistingEmailWhenCreatingUserThenExceptionIsThrown() {
        UserCreateDTO nonValidUserCreateDTO =
                UserCreateDTO.builder()
                        .nickname("anotherNickname")
                        .email(email)
                        .biography("another bio")
                        .password("anotherPassword")
                        .build();

        userService.createUser(userCreateDTO);

        assertThrows(
                UserWithThatEmailExistsException.class,
                () -> userService.createUser(nonValidUserCreateDTO));
    }

    @Test
    @WithMockUser(username = "some@email.com")
    void Test004_GivenAnExistingUserWhenDeletingItThenAUserWithThatNicknameAndEmailCanBeCreated() {
        User createdUser = userService.createUser(userCreateDTO);

        userService.deleteUser();

        UserCreateDTO anotherUserCreateDTO =
                UserCreateDTO.builder()
                        .nickname(nickname)
                        .email(email)
                        .biography("new bio")
                        .password("new password")
                        .build();

        User anotherCreatedUser = userService.createUser(anotherUserCreateDTO);

        assertNotEquals(createdUser.getId(), anotherCreatedUser.getId());

        assertEquals(nickname, anotherCreatedUser.getNickname());
        assertEquals(email, anotherCreatedUser.getEmail());
        assertEquals("new bio", anotherCreatedUser.getBiography());
        assertNotEquals("new password", anotherCreatedUser.getPassword());
        assertFalse(anotherCreatedUser.isActive());
    }

    @Test
    @WithMockUser
    void Test005_GivenANonExistentUserWhenDeletingThenExceptionIsThrown() {
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser());
    }

    @Test
    @WithMockUser(username = "some@email.com")
    void Test006_GivenAUserThatHasAProjectWhenDeletingTheUserThenTheProjectIsDeleted() {

        User user = userService.createUser(userCreateDTO);

        projectService.createProject(
                ProjectCreateDTO.builder()
                        .title("Project Title")
                        .description("description")
                        .tags(Arrays.asList("tag1", "tag2"))
                        .forumTags(Arrays.asList("help", "example"))
                        .links(Arrays.asList("link1", "link2"))
                        .languages(Arrays.asList("Java", "C"))
                        .build());

        assertEquals(1, projectService.getAllProjects().size());

        userService.deleteUser();

        assertTrue(projectService.getAllProjects().isEmpty());
    }

    @Test
    void Test007_GivenAValidValidTokenWhenConfirmAccountThenUserIsActiveEqualsTrue() {
        User user = userService.createUser(userCreateDTO);
        ConfirmationTokenDTO confirmationTokenDTO =
                ConfirmationTokenDTO.builder()
                        .confirmationToken("token001")
                        .id(user.getId())
                        .build();
        confirmationTokenDTO.setConfirmationToken(user.getConfirmationToken());

        User activatedUser = userService.confirmUser(confirmationTokenDTO);

        assertTrue(activatedUser.isActive());
    }

    @Test
    void Test008_GivenAnInvalidTokenWhenWantToConfirmAccountThenThrowExceptionInvalidToken() {
        User user = userService.createUser(userCreateDTO);
        String invalidToken = "token002";
        ConfirmationTokenDTO confirmationTokenDTO =
                ConfirmationTokenDTO.builder()
                        .confirmationToken("token001")
                        .id(user.getId())
                        .build();
        confirmationTokenDTO.setConfirmationToken(invalidToken);
        assertThrows(
                TokenConfirmationFailedException.class,
                () -> userService.confirmUser(confirmationTokenDTO));
    }

    @Test
    void
            Test009_GivenAnAlreadyActiveUserWhenWantToConfirmThatUserThenThrowTokenConfirmationFailedException() {
        User user = userService.createUser(userCreateDTO);
        ConfirmationTokenDTO confirmationTokenDTO =
                ConfirmationTokenDTO.builder()
                        .confirmationToken("token001")
                        .id(user.getId())
                        .build();
        confirmationTokenDTO.setConfirmationToken(user.getConfirmationToken());
        User validatedUser = userService.confirmUser(confirmationTokenDTO);

        assertThrows(
                TokenConfirmationFailedException.class,
                () -> userService.confirmUser(confirmationTokenDTO));
    }

    @Test
    @WithMockUser(username = "some@email.com")
    void Test010_GivenAUserAndAUserUpdateDTOWithAvailableNicknameWhenUpdatingUserThenItIsUpdated() {

        User persistedApplicationUser = userService.createUser(userCreateDTO);

        User updatedApplicationUser = userService.updateUser(userUpdateDTO);
        assertEquals(updatedNickname, updatedApplicationUser.getNickname());
        assertEquals(email, updatedApplicationUser.getEmail());
        assertEquals(updatedBiography, updatedApplicationUser.getBiography());
        assertNotEquals(updatedPassword, updatedApplicationUser.getPassword());
        assertFalse(updatedApplicationUser.isActive());

        assertEquals(persistedApplicationUser.getId(), updatedApplicationUser.getId());
        assertNotEquals(
                persistedApplicationUser.getNickname(), updatedApplicationUser.getNickname());
        assertNotEquals(
                persistedApplicationUser.getBiography(), updatedApplicationUser.getBiography());
        assertNotEquals(
                persistedApplicationUser.getPassword(), updatedApplicationUser.getPassword());
    }

    @Test
    @WithMockUser(username = "some@email.com")
    void
            Test011_GivenAUserAndAUserUpdateDTOWithSameNicknameAsBeforeWhenUpdatingUserThenItIsUpdated() {

        User persistedApplicationUser = userService.createUser(userCreateDTO);

        userUpdateDTO.setNickname(nickname);

        User updatedApplicationUser = userService.updateUser(userUpdateDTO);
        assertEquals(nickname, updatedApplicationUser.getNickname());
        assertEquals(email, updatedApplicationUser.getEmail());
        assertEquals(updatedBiography, updatedApplicationUser.getBiography());
        assertNotEquals(updatedPassword, updatedApplicationUser.getPassword());
        assertFalse(updatedApplicationUser.isActive());

        assertEquals(persistedApplicationUser.getId(), updatedApplicationUser.getId());
        assertEquals(persistedApplicationUser.getNickname(), updatedApplicationUser.getNickname());
        assertNotEquals(
                persistedApplicationUser.getBiography(), updatedApplicationUser.getBiography());
        assertNotEquals(
                persistedApplicationUser.getPassword(), updatedApplicationUser.getPassword());
    }

    @Test
    @WithMockUser
    void Test012_GivenANonExistentUserWhenUpdatingThenExceptionIsThrown() {
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(userUpdateDTO));
    }

    @Test
    @WithMockUser(username = "some@email.com")
    void
            Test013_GivenAUserAndAUserUpdateDTOWithTakenNicknameWhenUpdatingUserThenExceptionIsThrown() {
        userService.createUser(userCreateDTO);
        userService.createUser(
                UserCreateDTO.builder()
                        .nickname(updatedNickname)
                        .email("another@email.com")
                        .password("password")
                        .build());

        assertThrows(
                UserWithThatNicknameExistsException.class,
                () -> userService.updateUser(userUpdateDTO));
    }

    @Test
    void
            Test014_GivenaValidEmailandValidNewPasswordWhenWantToRecoverPasswordThenChangeTheOldPasswordForNewOne() {
        User user = userService.createUser(userCreateDTO);
        PasswordRecoveryDTO passwordRecoveryDTO =
                PasswordRecoveryDTO.builder()
                        .passwordRecoveryToken(passwordRecoveryToken)
                        .newPassword(newPassword)
                        .id(user.getId())
                        .build();

        passwordRecoveryDTO.setPasswordRecoveryToken(user.getPasswordRecoveryToken());

        ConfirmationTokenDTO confirmationTokenDTO =
                ConfirmationTokenDTO.builder()
                        .confirmationToken("token001")
                        .id(user.getId())
                        .build();
        confirmationTokenDTO.setConfirmationToken(user.getConfirmationToken());
        User userToBeUpdated = userService.confirmUser(confirmationTokenDTO);

        User passwordUpdatedUser = userService.recoverPassword(passwordRecoveryDTO);

        assertNotEquals(userToBeUpdated.getPassword(), passwordUpdatedUser.getPassword());

        System.out.println(passwordUpdatedUser.getPassword());
    }

    @Test
    void
            Test015_GivenAnInactiveUserWhenWantToRecoverPassWordThenThrowInvalidPasswordRecoveryException() {
        User user = userService.createUser(userCreateDTO);
        PasswordRecoveryDTO passwordRecoveryDTO =
                PasswordRecoveryDTO.builder()
                        .passwordRecoveryToken(passwordRecoveryToken)
                        .newPassword(newPassword)
                        .id(user.getId())
                        .build();

        passwordRecoveryDTO.setPasswordRecoveryToken(user.getPasswordRecoveryToken());

        assertThrows(
                InvalidPasswordRecoveryException.class,
                () -> userService.recoverPassword(passwordRecoveryDTO));
    }

    @Test
    void Test017_GivenAnInvalidPasswordRecoveryTokenWhenWantToRecoverPasswordThenThrowException() {
        User user = userService.createUser(userCreateDTO);
        PasswordRecoveryDTO passwordRecoveryDTO =
                PasswordRecoveryDTO.builder()
                        .passwordRecoveryToken(passwordRecoveryToken)
                        .newPassword(newPassword)
                        .id(user.getId())
                        .build();

        passwordRecoveryDTO.setPasswordRecoveryToken("passwordRecoveryToken002");
        ConfirmationTokenDTO confirmationTokenDTO =
                ConfirmationTokenDTO.builder()
                        .confirmationToken("token001")
                        .id(user.getId())
                        .build();
        confirmationTokenDTO.setConfirmationToken(user.getConfirmationToken());
        User activatedUser = userService.confirmUser(confirmationTokenDTO);
        assertThrows(
                TokenConfirmationFailedException.class,
                () -> userService.recoverPassword(passwordRecoveryDTO));
    }

    @Test
    void
            Test018_GivenAnInvalidPasswordLengthWhenWantToRecoverPasswordThenThrowPasswordRecoveryException() {
        User user = userService.createUser(userCreateDTO);
        PasswordRecoveryDTO passwordRecoveryDTO =
                PasswordRecoveryDTO.builder()
                        .passwordRecoveryToken(passwordRecoveryToken)
                        .newPassword(newPassword)
                        .id(user.getId())
                        .build();
        ConfirmationTokenDTO confirmationTokenDTO =
                ConfirmationTokenDTO.builder()
                        .confirmationToken("token001")
                        .id(user.getId())
                        .build();
        confirmationTokenDTO.setConfirmationToken(user.getConfirmationToken());
        User userToBeUpdated = userService.confirmUser(confirmationTokenDTO);
        passwordRecoveryDTO.setPasswordRecoveryToken(user.getPasswordRecoveryToken());
        passwordRecoveryDTO.setNewPassword("test001");

        assertThrows(
                PasswordRecoveryFailedException.class,
                () -> userService.recoverPassword(passwordRecoveryDTO));
    }

    @Test
    @WithMockUser(username = "some@email.com")
    void
            Test019_GivenAUserAndAValidPreferencesUpdateDTOWhenUpdatingPreferencesThenTheyAreUpdated() {
        userService.createUser(userCreateDTO);

        PreferencesUpdateDTO preferencesUpdateDTO =
                PreferencesUpdateDTO.builder().tags(tags).languages(languages).build();

        User userWithPreferences = userService.updatePreferences(preferencesUpdateDTO);
        assertNotNull(userWithPreferences.getId());
        assertEquals(tags, userWithPreferences.getPreferredTags());
        assertEquals(languages, userWithPreferences.getPreferredLanguages());
    }

    @Test
    @WithMockUser(username = "some@email.com")
    void
            Test020_GivenAUserAndAPreferencesUpdateDTOWithAnInvalidLanguageWhenUpdatingPreferencesThenExceptionIsThrown() {
        userService.createUser(userCreateDTO);

        List<String> invalidLanguages = List.of("Java", "C", "NotAValidLanguage");

        PreferencesUpdateDTO preferencesUpdateDTO =
                PreferencesUpdateDTO.builder().tags(tags).languages(invalidLanguages).build();

        assertThrows(
                LanguageNotValidException.class,
                () -> userService.updatePreferences(preferencesUpdateDTO));
    }
}
