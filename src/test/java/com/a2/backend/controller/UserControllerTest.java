package com.a2.backend.controller;

import com.a2.backend.entity.User;
import com.a2.backend.model.UserCreateDTO;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UserControllerTest {

    @Autowired
    TestRestTemplate restTemplate;

    private final String baseUrl = "/user";

    private final String nickname = "nickname";
    private final String email = "some@gmail.com";
    private final String biography = "bio";
    private final String password = "password";

    @Test
    void
    Test001_GivenAValidUserCreateDTOWhenRequestingPostThenReturnStatusCreatedAndPersistedUserAreReturned() {

        UserCreateDTO userCreateDTO =
                UserCreateDTO.builder()
                        .nickname(nickname)
                        .email(email)
                        .biography(biography)
                        .password(password)
                        .build();

        HttpEntity<UserCreateDTO> request = new HttpEntity<>(userCreateDTO);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, User.class);
        assertEquals(HttpStatus.CREATED, getResponse.getStatusCode());

        val persistedUser = getResponse.getBody();

        assertNotNull(persistedUser);
        assertEquals(nickname, persistedUser.getNickname());
        assertEquals(email, persistedUser.getEmail());
        assertEquals(biography, persistedUser.getBiography());
        assertNotEquals(password, persistedUser.getPassword());
        assertFalse(persistedUser.isActive());
    }

    @Test
    void
    Test002_GivenAUserCreateDTOWithInvalidNicknameWhenCreatingUserThenBadStatusResponseIsReturned() {
        String invalidNickname = "not a valid nickname as it is way too long";

        UserCreateDTO userCreateDTO =
                UserCreateDTO.builder()
                        .nickname(invalidNickname)
                        .email(email)
                        .biography(biography)
                        .password(password)
                        .build();

        HttpEntity<UserCreateDTO> request = new HttpEntity<>(userCreateDTO);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, User.class);
        assertEquals(HttpStatus.BAD_REQUEST, getResponse.getStatusCode());
    }

    @Test
    void
    Test003_GivenAUserCreateDTOWithInvalidEmailWhenCreatingUserThenBadStatusResponseIsReturned() {
        String invalidEmail = "this is not a real email";

        UserCreateDTO userCreateDTO =
                UserCreateDTO.builder()
                        .nickname(nickname)
                        .email(invalidEmail)
                        .biography(biography)
                        .password(password)
                        .build();

        HttpEntity<UserCreateDTO> request = new HttpEntity<>(userCreateDTO);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, User.class);
        assertEquals(HttpStatus.BAD_REQUEST, getResponse.getStatusCode());
    }

    @Test
    void
    Test004_GivenAUserCreateDTOWithInvalidPasswordWhenCreatingUserThenBadStatusResponseIsReturned() {
        String invalidPassword = "short";

        UserCreateDTO userCreateDTO =
                UserCreateDTO.builder()
                        .nickname(nickname)
                        .email(email)
                        .biography(biography)
                        .password(invalidPassword)
                        .build();

        HttpEntity<UserCreateDTO> request = new HttpEntity<>(userCreateDTO);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, User.class);
        assertEquals(HttpStatus.BAD_REQUEST, getResponse.getStatusCode());
    }

    @Test
    void
    Test005_GivenAUserCreateDTOWithNoBiographyWhenCreatingUserThenStatusIsCreatedAndBiographyIsNull() {

        UserCreateDTO userCreateDTO =
                UserCreateDTO.builder().nickname(nickname).email(email).password(password).build();

        HttpEntity<UserCreateDTO> request = new HttpEntity<>(userCreateDTO);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, User.class);
        assertEquals(HttpStatus.CREATED, getResponse.getStatusCode());

        val persistedUser = getResponse.getBody();
        assertNotNull(persistedUser);
        assertNull(persistedUser.getBiography());
    }

    @Test
    void
    Test006_GivenAUserCreateDTOWithExistingNicknameWhenCreatingUserThenExceptionIsHandledAndBadRequestIsReturned() {

        UserCreateDTO userCreateDTO =
                UserCreateDTO.builder()
                        .nickname(nickname)
                        .email(email)
                        .biography(biography)
                        .password(password)
                        .build();

        HttpEntity<UserCreateDTO> request = new HttpEntity<>(userCreateDTO);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, User.class);
        assertEquals(HttpStatus.CREATED, getResponse.getStatusCode());

        String anotherEmail = "another@gmail.com";
        String anotherBiography = "another bio";
        String anotherPassword = "anotherPassword";

        UserCreateDTO sameNicknameUserCreateDTO =
                UserCreateDTO.builder()
                        .nickname(nickname)
                        .email(anotherEmail)
                        .biography(anotherBiography)
                        .password(anotherPassword)
                        .build();

        HttpEntity<UserCreateDTO> anotherRequest = new HttpEntity<>(sameNicknameUserCreateDTO);

        val anotherGetResponse =
                restTemplate.exchange(baseUrl, HttpMethod.POST, anotherRequest, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, anotherGetResponse.getStatusCode());
    }

    @Test
    void
    Test007_GivenAUserCreateDTOWithExistingEmailWhenCreatingUserThenExceptionIsHandledAndBadRequestIsReturned() {

        UserCreateDTO userCreateDTO =
                UserCreateDTO.builder()
                        .nickname(nickname)
                        .email(email)
                        .biography(biography)
                        .password(password)
                        .build();

        HttpEntity<UserCreateDTO> request = new HttpEntity<>(userCreateDTO);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, User.class);
        assertEquals(HttpStatus.CREATED, getResponse.getStatusCode());

        String anotherNickname = "anotherNickname";
        String anotherBiography = "another bio";
        String anotherPassword = "anotherPassword";

        UserCreateDTO sameNicknameUserCreateDTO =
                UserCreateDTO.builder()
                        .nickname(anotherNickname)
                        .email(email)
                        .biography(anotherBiography)
                        .password(anotherPassword)
                        .build();

        HttpEntity<UserCreateDTO> anotherRequest = new HttpEntity<>(sameNicknameUserCreateDTO);

        val anotherGetResponse =
                restTemplate.exchange(baseUrl, HttpMethod.POST, anotherRequest, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, anotherGetResponse.getStatusCode());
    }
}
