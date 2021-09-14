package com.a2.backend.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.a2.backend.entity.User;
import com.a2.backend.exception.TokenConfirmationFailedException;
import com.a2.backend.exception.UserNotFoundException;
import com.a2.backend.exception.UserWithThatEmailExistsException;
import com.a2.backend.exception.UserWithThatNicknameExistsException;
import com.a2.backend.model.ProjectCreateDTO;
import com.a2.backend.model.UserCreateDTO;
import com.a2.backend.service.ProjectService;
import com.a2.backend.service.UserService;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UserServiceImplTest {

    @Autowired private UserService userService;

    @Autowired private ProjectService projectService;

    String nickname = "nickname";
    String email = "some@email.com";
    String biography = "bio";
    String password = "password";
    String confirmationToken = "token001";

    UserCreateDTO userCreateDTO =
            UserCreateDTO.builder()
                    .nickname(nickname)
                    .email(email)
                    .biography(biography)
                    .password(password)
                    .confirmationToken(confirmationToken)
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
}
