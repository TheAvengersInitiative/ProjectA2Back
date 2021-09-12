package com.a2.backend.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.a2.backend.entity.User;
import com.a2.backend.exception.TokenConfirmationFailedException;
import com.a2.backend.exception.UserWithThatEmailExistsException;
import com.a2.backend.exception.UserWithThatNicknameExistsException;
import com.a2.backend.model.UserCreateDTO;
import com.a2.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UserServiceImplTest {

    @Autowired private UserService userService;
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
                        .nickname("nickname")
                        .email("another@email.com")
                        .biography("another bio")
                        .password("anotherPassword")
                        .build();

        userService.createUser(userCreateDTO);

        assertThrows(
                UserWithThatNicknameExistsException.class,
                () -> {
                    userService.createUser(nonValidUserCreateDTO);
                });
    }

    @Test
    void Test003_GivenAUserCreateDTOWithAnExistingEmailWhenCreatingUserThenExceptionIsThrown() {
        UserCreateDTO nonValidUserCreateDTO =
                UserCreateDTO.builder()
                        .nickname("anotherNickname")
                        .email("some@email.com")
                        .biography("another bio")
                        .password("anotherPassword")
                        .build();

        userService.createUser(userCreateDTO);

        assertThrows(
                UserWithThatEmailExistsException.class,
                () -> {
                    userService.createUser(nonValidUserCreateDTO);
                });
    }

    @Test
    void Test006_GivenAvalidValidTokenWhenConfirmAccountThenUserisActiveEqualsTrue() {
        User user = userService.createUser(userCreateDTO);
        String confirmationToken1 = "token001";

        User activatedUser = userService.confirmUser(confirmationToken1, user.getId());

        assertTrue(activatedUser.isActive());
    }

    @Test
    void Test007_GivenAnInvalidTokenWhenWantToConfirmAccountThenThrowexceptionInvalidToken() {
        User user = userService.createUser(userCreateDTO);
        String invalidToken = "token002";

        assertThrows(
                TokenConfirmationFailedException.class,
                () -> userService.confirmUser(invalidToken, user.getId()));
    }

    @Test
    void
            Test008_GivenAnAlreadyActiveUserWhenWantToConfirmThatUserThenThrowTokenConfirmationFailedException() {
        User user = userService.createUser(userCreateDTO);
        String validToken = "token001";

        User validatedUser = userService.confirmUser(validToken, user.getId());

        assertThrows(
                TokenConfirmationFailedException.class,
                () -> userService.confirmUser(validToken, validatedUser.getId()));
    }
}
