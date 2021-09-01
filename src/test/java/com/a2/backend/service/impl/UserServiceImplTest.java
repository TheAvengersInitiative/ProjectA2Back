package com.a2.backend.service.impl;

import com.a2.backend.entity.User;
import com.a2.backend.exception.UserWithThatEmailExistsException;
import com.a2.backend.exception.UserWithThatNicknameExistsException;
import com.a2.backend.model.UserCreateDTO;
import com.a2.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    String nickname = "nickname";
    String email = "some@email.com";
    String biography = "bio";
    String password = "password";

    UserCreateDTO userCreateDTO =
            UserCreateDTO.builder()
                    .nickname(nickname)
                    .email(email)
                    .biography(biography)
                    .password(password)
                    .build();

    @Test
    void
    Test001_GivenAValidUserCreateDTOWhenCreatingUserThenThePersistedUserWithHashedPasswordIsReturned() {
        User persistedUser = userService.createUser(userCreateDTO);

        assertEquals(nickname, persistedUser.getNickname());
        assertEquals(email, persistedUser.getEmail());
        assertEquals(biography, persistedUser.getBiography());
        assertNotEquals(password, persistedUser.getPassword());
        assertFalse(persistedUser.isActive());
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
}
