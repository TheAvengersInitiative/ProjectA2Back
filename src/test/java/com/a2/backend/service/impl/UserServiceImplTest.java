package com.a2.backend.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.a2.backend.entity.User;
import com.a2.backend.exception.*;
import com.a2.backend.model.*;
import com.a2.backend.service.ProjectService;
import com.a2.backend.service.UserService;
import java.util.Arrays;
import java.util.Arrays;
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
class UserServiceImplTest {

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

    PasswordRecoveryDTO passwordRecoveryDTO =
            PasswordRecoveryDTO.builder()
                    .passwordRecoveryToken(passwordRecoveryToken)
                    .newPassword(newPassword)
                    .email(email)
                    .build();

    PasswordRecoveryInitDTO passwordRecoveryInitDTO =
            PasswordRecoveryInitDTO.builder().email(email).build();
    UserCreateDTO userCreateDTO =
            UserCreateDTO.builder()
                    .nickname(nickname)
                    .email(email)
                    .biography(biography)
                    .password(password)
                    .confirmationToken(confirmationToken)
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
                        .owner(user)
                        .tags(Arrays.asList("tag1", "tag2"))
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
        String confirmationToken1 = "token001";

        User activatedUser = userService.confirmUser(confirmationToken1, user.getId());

        assertTrue(activatedUser.isActive());
    }

    @Test
    void Test008_GivenAnInvalidTokenWhenWantToConfirmAccountThenThrowExceptionInvalidToken() {
        User user = userService.createUser(userCreateDTO);
        String invalidToken = "token002";

        assertThrows(
                TokenConfirmationFailedException.class,
                () -> userService.confirmUser(invalidToken, user.getId()));
    }

    @Test
    void
            Test009_GivenAnAlreadyActiveUserWhenWantToConfirmThatUserThenThrowTokenConfirmationFailedException() {
        User user = userService.createUser(userCreateDTO);
        String validToken = "token001";

        User validatedUser = userService.confirmUser(validToken, user.getId());

        assertThrows(
                TokenConfirmationFailedException.class,
                () -> userService.confirmUser(validToken, validatedUser.getId()));
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
            Test010_GivenAUserAndAUserUpdateDTOWithSameNicknameAsBeforeWhenUpdatingUserThenItIsUpdated() {

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
    void Test011_GivenANonExistentUserWhenUpdatingThenExceptionIsThrown() {
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(userUpdateDTO));
    }

    @Test
    @WithMockUser(username = "some@email.com")
    void
            Test012_GivenAUserAndAUserUpdateDTOWithTakenNicknameWhenUpdatingUserThenExceptionIsThrown() {
        userService.createUser(userCreateDTO);
        userService.createUser(
                UserCreateDTO.builder()
                        .nickname(updatedNickname)
                        .email("another@email.com")
                        .password("password")
                        .confirmationToken("token002")
                        .build());

        assertThrows(
                UserWithThatNicknameExistsException.class,
                () -> userService.updateUser(userUpdateDTO));
    }

    @Test
    void
            Test009_GivenaValidEmailandValidNewPasswordWhenWantToRecoverPasswordThenChangeTheOldPasswordForNewOne() {
        User user = userService.createUser(userCreateDTO);
        passwordRecoveryDTO.setPasswordRecoveryToken(user.getPasswordRecoveryToken());

        User userToBeUpdated = userService.confirmUser("token001", user.getId());

        User passwordUpdatedUser = userService.recoverPassword(passwordRecoveryDTO);

        assertNotEquals(userToBeUpdated.getPassword(), passwordUpdatedUser.getPassword());

        System.out.println(passwordUpdatedUser.getPassword());
    }

    @Test
    void
            Test010_GivenAnInactiveUserWhenWantToRecoverPassWordThenThrowInvalidPasswordRecoveryException() {
        User user = userService.createUser(userCreateDTO);
        passwordRecoveryDTO.setPasswordRecoveryToken(user.getPasswordRecoveryToken());

        assertThrows(
                InvalidPasswordRecoveryException.class,
                () -> userService.recoverPassword(passwordRecoveryDTO));
    }

    @Test
    void
            Test011_GivenAnInvalidEmailWhenWantToRecoverPasswordThenThrowInvalidPasswordRecoveryException() {
        passwordRecoveryDTO.setEmail("adihajkd");
        assertThrows(
                InvalidPasswordRecoveryException.class,
                () -> userService.recoverPassword(passwordRecoveryDTO));
    }

    @Test
    void Test012_GivenAnInvalidPasswordRecoveryTokenWhenWantToRecoverPasswordThenThrowException() {
        User user = userService.createUser(userCreateDTO);
        passwordRecoveryDTO.setPasswordRecoveryToken("passwordRecoveryToken002");
        User activatedUser = userService.confirmUser(confirmationToken, user.getId());
        assertThrows(
                TokenConfirmationFailedException.class,
                () -> userService.recoverPassword(passwordRecoveryDTO));
    }

    @Test
    void Test013_GivenAnInvalidPasswordLengthWhenWantToRecoverPasswordThenReturnNull() {
        User user = userService.createUser(userCreateDTO);
        User userToBeUpdated = userService.confirmUser(confirmationToken, user.getId());
        passwordRecoveryDTO.setPasswordRecoveryToken(user.getPasswordRecoveryToken());
        passwordRecoveryDTO.setNewPassword("test001");
        passwordRecoveryDTO.setEmail(userToBeUpdated.getEmail());

        assertThrows(
                PasswordRecoveryFailedException.class,
                () -> userService.recoverPassword(passwordRecoveryDTO));
    }
}
