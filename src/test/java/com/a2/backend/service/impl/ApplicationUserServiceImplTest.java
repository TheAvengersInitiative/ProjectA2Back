package com.a2.backend.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.a2.backend.entity.ApplicationUser;
import com.a2.backend.exception.UserWithThatEmailExistsException;
import com.a2.backend.exception.UserWithThatNicknameExistsException;
import com.a2.backend.model.UserCreateDTO;
import com.a2.backend.service.ApplicationUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ApplicationUserServiceImplTest {

    @Autowired private ApplicationUserService applicationUserService;
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
        ApplicationUser persistedApplicationUser = applicationUserService.createUser(userCreateDTO);
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

        applicationUserService.createUser(userCreateDTO);

        assertThrows(
                UserWithThatNicknameExistsException.class,
                () -> {
                    applicationUserService.createUser(nonValidUserCreateDTO);
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

        applicationUserService.createUser(userCreateDTO);

        assertThrows(
                UserWithThatEmailExistsException.class,
                () -> {
                    applicationUserService.createUser(nonValidUserCreateDTO);
                });
    }
}
